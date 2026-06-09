package io.conduktor.demo.order.kafka;

import io.conduktor.demo.order.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private static final String TOPIC = "order-events";
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void publishOrderEvent(OrderEvent event) {
        log.info("Publishing order event: {} for orderId: {}", event.getEventType(), event.getOrderId());
        kafkaTemplate.send(TOPIC, event.getOrderId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish order event", ex);
                    } else {
                        log.info("Order event published to partition: {}",
                                result.getRecordMetadata().partition());
                    }
                });
    }
}
