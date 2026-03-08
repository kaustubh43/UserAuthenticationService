package org.ecommerce.userauthenticationservice.clients;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaProducerClientTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private KafkaProducerClient kafkaProducerClient;

    @BeforeEach
    void setUp() {
        kafkaProducerClient = new KafkaProducerClient(kafkaTemplate);
    }

    @Test
    @DisplayName("should delegate sendMessage to KafkaTemplate.send with the correct topic and message")
    void sendMessage_DelegatesToKafkaTemplate() {
        kafkaProducerClient.sendMessage("EMAIL_SIGNUP", "{\"to\":\"user@example.com\"}");

        verify(kafkaTemplate).send("EMAIL_SIGNUP", "{\"to\":\"user@example.com\"}");
    }

    @Test
    @DisplayName("should pass the exact topic string to KafkaTemplate")
    void sendMessage_PassesCorrectTopic() {
        kafkaProducerClient.sendMessage("MY_TOPIC", "payload");

        verify(kafkaTemplate).send("MY_TOPIC", "payload");
    }

    @Test
    @DisplayName("should pass the exact message string to KafkaTemplate")
    void sendMessage_PassesCorrectMessage() {
        String message = "{\"key\":\"value\"}";
        kafkaProducerClient.sendMessage("TOPIC", message);

        verify(kafkaTemplate).send("TOPIC", message);
    }
}
