package io.conduktor.demo.order.service;

import io.conduktor.demo.order.event.OrderEvent;
import io.conduktor.demo.order.event.OrderItemDto;
import io.conduktor.demo.order.kafka.OrderEventProducer;
import io.conduktor.demo.order.model.Order;
import io.conduktor.demo.order.model.OrderStatus;
import io.conduktor.demo.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    public Order placeOrder(Order order) {
        order.setStatus(OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);

        // Publish to Kafka → Delivery Service will consume this
        OrderEvent event = OrderEvent.builder()
                .eventType("ORDER_PLACED")
                .orderId(savedOrder.getId())
                .restaurantId(savedOrder.getRestaurantId())
                .customerId(savedOrder.getCustomerId())
                .deliveryAddress(savedOrder.getDeliveryAddress())
                .totalAmount(savedOrder.getTotalAmount())
                .items(savedOrder.getItems().stream()
                        .map(i -> new OrderItemDto(
                                i.getMenuItemId(), i.getItemName(),
                                i.getQuantity(), i.getPrice()))
                        .collect(Collectors.toList()))
                .timestamp(LocalDateTime.now())
                .build();

        orderEventProducer.publishOrderEvent(event);
        log.info("Order placed and event published. OrderId: {}", savedOrder.getId());
        return savedOrder;
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public Order cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() == OrderStatus.OUT_FOR_DELIVERY ||
                order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel order in status: " + order.getStatus());
        }
        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);

        // Publish cancellation event
        OrderEvent event = OrderEvent.builder()
                .eventType("ORDER_CANCELLED")
                .orderId(orderId)
                .timestamp(LocalDateTime.now())
                .build();
        orderEventProducer.publishOrderEvent(event);
        return saved;
    }
}
