package com.example.ledger;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    public static final String TOPIC = "payment.completed";
    public static final String DLT = "payment.completed.DLT";

    /** Dead-letter topic. Same partition count as the source topic (the recoverer keeps the partition number). */
    @Bean
    NewTopic paymentCompletedDlt() {
        return TopicBuilder.name(DLT).partitions(3).replicas(1).build();
    }

    /**
     * Retry up to 3 more times, 1 second apart. If it still fails, publish the
     * record to payment.completed.DLT and move on, so one bad message never blocks the partition.
     * Poison pills (JSON that cannot be parsed) are not retried: retrying cannot fix them.
     */
    @Bean
    DefaultErrorHandler errorHandler(KafkaTemplate<String, String> template) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template);
        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
        handler.addNotRetryableExceptions(com.fasterxml.jackson.core.JsonProcessingException.class);
        return handler;
    }
}
