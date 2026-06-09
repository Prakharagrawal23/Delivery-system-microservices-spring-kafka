package io.conduktor.demo.order.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private String eventType;         // ORDER_PLACED, ORDER_CANCELLED, ORDER_UPDATED
    private Long orderId;
    private Long restaurantId;
    private Long customerId;
    private String deliveryAddress;
    private Double totalAmount;
    private List<OrderItemDto> items;
    private LocalDateTime timestamp;
}
