package io.conduktor.demo.delivery.kafka;

import io.conduktor.demo.delivery.event.DeliveryStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryStatusProducer {

    private static final String TOPIC = "delivery-status";
    private final KafkaTemplate<String, DeliveryStatusEvent> kafkaTemplate;

    public void publishStatus(DeliveryStatusEvent event) {
        log.info("Publishing delivery status: {} for orderId: {}", event.getStatus(), event.getOrderId());
        kafkaTemplate.send(TOPIC, event.getOrderId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish delivery status", ex);
                    } else {
                        log.info("Delivery status published.");
                    }
                });
    }
}