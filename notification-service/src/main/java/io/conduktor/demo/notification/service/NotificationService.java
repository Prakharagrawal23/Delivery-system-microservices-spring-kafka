package io.conduktor.demo.notification.service;


import io.conduktor.demo.notification.event.DeliveryStatusEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void sendNotification(DeliveryStatusEvent event) {
        // In production: integrate with SMS (Twilio), Push (FCM), or Email (SES)
        // For now we log a detailed notification

        String notification = String.format(
                """
                ========== NOTIFICATION ==========
                To Customer ID : %d
                Order ID       : %d
                Status         : %s
                Message        : %s
                Delivery By    : %s
                Location       : %s
                Time           : %s
                ==================================
                """,
                event.getCustomerId(),
                event.getOrderId(),
                event.getStatus(),
                event.getMessage(),
                event.getDeliveryPersonName(),
                event.getCurrentLocation(),
                event.getTimestamp()
        );

        log.info(notification);

        // Example: you can wire in Twilio here
        // twilioClient.sendSMS(customerPhone, event.getMessage());
    }
}
