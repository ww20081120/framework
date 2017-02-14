package com.hbasesoft.framework.message.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.message.core.bean.MessageConfig;

public class MessageHelper {

    private static Map<String, Object> producerConfig = null;

    private static Map<String, Object> consumerConfigs = null;

    public static void send(String queue, byte[] message, MessageConfig mcf) {

	KafkaProducer<byte[], byte[]> kafkaProducer = new KafkaProducer<byte[], byte[]>(getProducerConfig());
	ProducerRecord<byte[], byte[]> kafkaRecord = new ProducerRecord<byte[], byte[]>(queue, message);
	kafkaProducer.send(kafkaRecord, new Callback() {
	    public void onCompletion(RecordMetadata metadata, Exception e) {
		if (null != e) {
		    LoggerUtil.error(e.getMessage(), e);
		}
		LoggerUtil.info("complete!");
	    }
	});
	kafkaProducer.flush();
	kafkaProducer.close();
    }

    public static List<byte[]> receive(String queue) {
	KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<byte[], byte[]>(getConsumerConfigs());
	consumer.subscribe(Arrays.asList(queue));
	byte[] result = null;

	ConsumerRecords<byte[], byte[]> records = consumer.poll(150);
	List<byte[]> list = new LinkedList<byte[]>();
	for (ConsumerRecord<byte[], byte[]> record : records) {
	    result = record.value();
	    list.add(result);
	}
	consumer.commitSync();
	consumer.close();
	return list;
    }

    public static Map<String, Object> getProducerConfig() {
	if (producerConfig == null) {
	    producerConfig = new HashMap<>();
	    producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
		    PropertyHolder.getProperty("kafka.broker.address") == null ? "127.0.0.1:9092"
			    : PropertyHolder.getProperty("kafka.broker.address"));
	    producerConfig.put(ProducerConfig.ACKS_CONFIG, "all");
	    producerConfig.put(ProducerConfig.RETRIES_CONFIG, 0);
	    producerConfig.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
	    producerConfig.put(ProducerConfig.LINGER_MS_CONFIG, 1);
	    producerConfig.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
	    producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
	    producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
	}

	return producerConfig;
    }

    public static Map<String, Object> getConsumerConfigs() {
	if (consumerConfigs == null) {
	    consumerConfigs = new HashMap<>();
	    consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, "group-test");
	    consumerConfigs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
		    PropertyHolder.getProperty("kafka.broker.address") == null ? "127.0.0.1:9092"
			    : PropertyHolder.getProperty("kafka.broker.address"));
	    consumerConfigs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
	    consumerConfigs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "none");
	    consumerConfigs.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
	    consumerConfigs.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
	    consumerConfigs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
	    consumerConfigs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
	}
	return consumerConfigs;
    }
}
