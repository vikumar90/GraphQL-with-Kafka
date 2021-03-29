package com.selflearn.model;

import lombok.Data;

/**
 * @author ViKumar
 *
 */
@Data
public class ExchangeNotification {
	
	private String exchangeCode;
	private String exchangeName;
	private String segmentMic;
	private String newExchangeCode;
	private String newExchangeName;
	private String eventInsertDate;
	private String eventSubject;
	private String eventSummaryText;
}