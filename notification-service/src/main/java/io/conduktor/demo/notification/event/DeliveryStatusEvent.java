package io.conduktor.demo.notification.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStatusEvent {
    private Long orderId;
    private Long customerId;
    private String status;
    private String deliveryPersonName;
    private String currentLocation;
    private String message;
    private LocalDateTime timestamp;
}
