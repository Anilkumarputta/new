package com.editorial.platform.event.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.editorial.platform.event.KafkaTopics;
import com.editorial.platform.event.model.ContentEvent;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic contentEventsTopic() {
        return new NewTopic(KafkaTopics.CONTENT_EVENTS, 1, (short) 1);
    }

    @Bean
    public ProducerFactory<String, ContentEvent> producerFactory(KafkaProperties kafkaProperties) {
        var properties = kafkaProperties.buildProducerProperties();
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<String, ContentEvent> kafkaTemplate(ProducerFactory<String, ContentEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConsumerFactory<String, ContentEvent> consumerFactory(KafkaProperties kafkaProperties) {
        var properties = kafkaProperties.buildConsumerProperties();
        JsonDeserializer<ContentEvent> jsonDeserializer = new JsonDeserializer<>(ContentEvent.class);
        jsonDeserializer.addTrustedPackages("com.editorial.platform.event.model");
        return new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ContentEvent> kafkaListenerContainerFactory(
        ConsumerFactory<String, ContentEvent> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ContentEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
