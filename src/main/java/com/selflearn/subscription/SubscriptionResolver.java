package com.selflearn.subscription;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.selflearn.filter.ExchangeNotificationFilter;
import com.selflearn.model.ExchangeNotification;

import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

/**
 * @author ViKumar
 * 
 *         This is subscription resolver
 * 
 *         for
 * 
 * 
 *         subscription { getNotifications(exchangeCode: "NYSE") { exchangeCode
 *         segmentMic exchangeName eventSubject eventSummaryText exchangeName
 *         newExchangeCode } }
 * 
 *         It listen to kafka topic and changes in that will reflect on Client
 *         UI message from kafka :
 * 
 *         { "exchangeCode": "NYSE", "exchangeName": "HONG KONG FUTURES EXCHANGE
 *         LIMITED", "segmentMic": "HKFE99", "newExchangeCode": "XOSE1",
 *         "eventInsertDate": "2020-02-20", "eventSubject": "Trading and
 *         Clearing Operational Arrangements Relating to the Capital Adjustment
 *         of New World Development Company Limited Stock Option Contracts –
 *         Share", "eventSummaryText": "Participants are requested to note that
 *         capital adjustment to the stock option contracts below will be made
 *         on the Effective Date of Share Consolidation" }
 *
 * 
 * 
 */
@Component
@Slf4j
public class SubscriptionResolver implements GraphQLSubscriptionResolver {

	@Value("${bootStrapServer}")
	private String bootStrapServer;

	@Value("${clientId}")
	private String clientId;

	@Value("${groupId}")
	private String groupId;

	@Value("${topic}")
	private String topic;

	private ReceiverOptions<Integer, String> receiverOptions;

	private Gson gson = new GsonBuilder().create();

	public Publisher<ExchangeNotification> getNotifications(ExchangeNotificationFilter exchangeNotificationFilter) {

		return consumeMessagesWithFilter(topic, exchangeNotificationFilter);//

	}

	public Flux<ExchangeNotification> consumeMessagesWithFilter(String topic,
			ExchangeNotificationFilter exchangeNotificationFilter) {

		configureKafkaProperties();

		ReceiverOptions<Integer, String> options = receiverOptions.subscription(Collections.singleton(topic))
				.addAssignListener(partitions -> log.debug("onPartitionsAssigned {}", partitions))
				.addRevokeListener(partitions -> log.debug("onPartitionsRevoked {}", partitions));

		return KafkaReceiver.create(options).receive().map(e -> {
			ExchangeNotification exchangeNotification = gson.fromJson(e.value(), ExchangeNotification.class);

			if ((exchangeNotification != null) && (checkIfNotificationIsApplicableForSubscriber(exchangeNotification,
					exchangeNotificationFilter))) { // filter out the notifications

				return exchangeNotification;

			} else {
				return new ExchangeNotification();
			}
		});

	}

	private void configureKafkaProperties() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put("duration", "10000");
		receiverOptions = ReceiverOptions.create(props);
	}

	private boolean checkIfNotificationIsApplicableForSubscriber(ExchangeNotification exchangeNotification,
			ExchangeNotificationFilter exchangeNotificationFilter) {

		boolean isExchangeCodeMatched = Arrays.stream(exchangeNotificationFilter.getExchangeCode())
				.anyMatch(exchangeNotification.getExchangeCode()::contains);

		boolean isSegmentMicsMatched = Arrays.stream(exchangeNotificationFilter.getSegmentMic())
				.anyMatch(exchangeNotification.getSegmentMic()::contains);

		return (isExchangeCodeMatched && isSegmentMicsMatched);

	}

}
