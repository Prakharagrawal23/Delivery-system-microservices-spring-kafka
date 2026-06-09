package io.conduktor.demo.delivery.kafka;

import io.conduktor.demo.delivery.event.OrderEvent;
import io.conduktor.demo.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final DeliveryService deliveryService;

    @KafkaListener(
            topics = "order-events",
            groupId = "delivery-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderEvent(OrderEvent event) {
        log.info("Delivery Service received order event: {} for orderId: {}",
                event.getEventType(), event.getOrderId());

        switch (event.getEventType()) {
            case "ORDER_PLACED" -> deliveryService.assignDelivery(event);
            case "ORDER_CANCELLED" -> deliveryService.cancelDelivery(event.getOrderId());
            default -> log.warn("Unknown event type: {}", event.getEventType());
        }
    }
}
