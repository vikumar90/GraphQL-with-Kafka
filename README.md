# GraphQL-with-Kafka
GraphQL With Kafka


In this example , We are able to acheive following: 

1.	Read message from Kafka queue, convert this message to event and pass it to the GraphQL subscriber.
2.	Explore about Subscriptions in GraphQL.
3.	Integration of posting events on Kafka Queue, consumption of events from Kafka Queue as Flux (Reactor-Kafka), and redirection of events to subscriber.

Steps to test this :
1. Bring the Kafka up , post the message on "test" topic.
2. Launch GraphqlPubSubLauncher as java application
3. Launch http://localhost:8085/graphiql
4. Hit below Quey


subscription {
  getNotifications(filter: {exchangeCode: ["NSE", "ABC"]}) {
    eventSummaryText
    eventSubject
    eventInsertDate
  }
}

5. Verify the message is delivered at client end.

