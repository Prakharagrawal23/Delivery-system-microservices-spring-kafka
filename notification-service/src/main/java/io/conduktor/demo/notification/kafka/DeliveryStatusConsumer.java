package io.conduktor.demo.notification.kafka;

import io.conduktor.demo.notification.event.DeliveryStatusEvent;
import io.conduktor.demo.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryStatusConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "delivery-status",
            groupId = "notification-service-group"
    )
    public void consume(DeliveryStatusEvent event) {
        log.info("Notification Service received: status={} for orderId={}",
                event.getStatus(), event.getOrderId());
        notificationService.sendNotification(event);
    }
}
