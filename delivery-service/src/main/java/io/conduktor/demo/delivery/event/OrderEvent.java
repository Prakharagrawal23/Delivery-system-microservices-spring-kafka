package io.conduktor.demo.delivery.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private String eventType;
    private Long orderId;
    private Long restaurantId;
    private Long customerId;
    private String deliveryAddress;
    private Double totalAmount;
    private List<OrderItemDto> items;
    private LocalDateTime timestamp;
}
