package com.jbryan98.bookingapp.orchestrator.infrastucture.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        // Dirección del broker Kafka (o lista de brokers separados por coma en producción)
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // La clave del mensaje se serializa como String (orderId como UUID string).
        // Usar la clave permite que mensajes del mismo orden vayan a la misma partición
        // → garantiza orden de procesamiento por orden.
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // El valor (el evento) se serializa como JSON con Jackson 3 (Jackson integrado en Spring Boot 4).
        // JacksonJsonSerializer reemplaza al deprecado JsonSerializer desde Spring Kafka 4.0.
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);

        // No incluir cabeceras __TypeId__ en los mensajes.
        // El consumidor infiere el tipo target desde la firma del @KafkaListener (ByteArrayJacksonJsonMessageConverter).
        // Esto simplifica la deserialización cross-service y evita acoplamiento de packages.
        config.put(JacksonJsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        // Productor idempotente: garantiza exactly-once delivery ante reintentos de red.
        // Activa automáticamente: acks=all, retries>0, max.in.flight.requests=5.
        // Imprescindible para evitar duplicar eventos de negocio (OrderCompleted × 2).
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
