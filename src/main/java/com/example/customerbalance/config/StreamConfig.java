package com.example.customerbalance.config;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Repartitioned;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;


import com.example.customerbalance.model.Customer;
import com.example.customerbalance.model.CustomerBalance;
import com.example.customerbalance.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableKafkaStreams
@RequiredArgsConstructor
public class StreamConfig {

	// Input topics
	public static final String CUSTOMER_TOPIC = "customer";
	public static final String BALANCE_TOPIC = "balance";
	public static final String CUSTOMER_BALACE_TOPIC = "customerbalance";
	
	private static final String CUSTOMER_REKEY_TOPIC = "Customer_rekey";
	
	private static final String BALANCE_REKEY_TOPIC = "Balance_rekey";
	// Output topics
	public static final String PRODUCTS_TOPIC = "products";
	private static final String CUSTOMER_BALANCE_STORE_NAME = "product_store";

	private final ObjectMapper mapper=null;

	@Bean
	public KStream<String, CustomerBalance> kStream(StreamsBuilder streamBuilder) {
		// First we define the various serialization/deserialization objects we will need
		
		var stringSerde = Serdes.String();
		var customerBalanceSerde = jsonSerde(CustomerBalance.class);
		var customerBalanceConsumed = Consumed.with(stringSerde, customerBalanceSerde);
		var customerSerde = jsonSerde(Customer.class);
		var customerConsumed = Consumed.with(stringSerde, customerSerde);
		var balanceSerde = jsonSerde(Transaction.class);
		var balanceConsumed = Consumed.with(stringSerde, balanceSerde);
		var customerBlanceProduced = Produced.with(stringSerde, customerBalanceSerde);


		// Group customer
		// - We first stream the customer
		// - Then we change the key of the message using select key so we can 'join' customer with balance
		// - Before we can join though, we need to re-repartition the customer to make sure customer and balance
		// with the same key end up in the same partitions.
		// - We can then group the customer by key.
		var groupedCustomer = streamBuilder.stream(CUSTOMER_TOPIC, customerConsumed)
				.selectKey((key, customer) -> customer.getAccountId())
				.repartition(Repartitioned.<String, Customer>as(CUSTOMER_REKEY_TOPIC)
						.withKeySerde(stringSerde)
						.withValueSerde(customerSerde))
				.groupByKey();
		
				
		
		var groupedBalance = streamBuilder.stream(BALANCE_TOPIC, balanceConsumed)
				.selectKey((key, balance) -> balance.getAccountId())
				.repartition(Repartitioned.<String, Transaction>as(BALANCE_REKEY_TOPIC)
						.withKeySerde(stringSerde)
						.withValueSerde(balanceSerde))
				.groupByKey();

	
	
		var groupedCustomerBalance = streamBuilder.stream(CUSTOMER_BALACE_TOPIC, customerBalanceConsumed)
				.mapValues((id, customerBalance) -> {
					customerBalance.setAccountId(id);
					return customerBalance;
				})
				.groupByKey();

		// Now our parts are ready to be merged using the co-group operation.
		// For each of our grouped stream we define how it's gonna be merged in the final Product.
		// We also define how to create the initial Product.
		// Finally we check that our product is complete before we send it to the output topic.
		var customerBalanceDetails = groupedCustomerBalance
				.<CustomerBalance>cogroup((key, customerBlance, customerBal) -> customerBal.mergeDetails(customerBlance))
				.cogroup(groupedCustomer, (key, customerDetails, customerBal) -> customerBal.mergeCustomer(customerDetails))
				.cogroup(groupedBalance, (key, balanceDetails, customerBal) -> customerBal.mergeBalance(balanceDetails))
				.aggregate(CustomerBalance::new,
						Materialized.<String, CustomerBalance, KeyValueStore<Bytes, byte[]>>as(CUSTOMER_BALANCE_STORE_NAME)
								.withKeySerde(stringSerde)
								.withValueSerde(customerBalanceSerde))
				.toStream()
				.filter((key, customerBal) -> customerBal.isComplete());

		customerBalanceDetails.to(CUSTOMER_BALACE_TOPIC, customerBlanceProduced);

		return customerBalanceDetails;
	}

	private <T> Serde<T> jsonSerde(Class<T> targetClass) {
		return new JsonSerde<>(targetClass, mapper).noTypeInfo().ignoreTypeHeaders();
	}
}
