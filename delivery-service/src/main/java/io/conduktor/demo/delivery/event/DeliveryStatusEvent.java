package io.conduktor.demo.delivery.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStatusEvent {
    private Long orderId;
    private Long customerId;
    private String status;            // ASSIGNED, PICKED_UP, IN_TRANSIT, DELIVERED
    private String deliveryPersonName;
    private String currentLocation;
    private String message;
    private LocalDateTime timestamp;
}