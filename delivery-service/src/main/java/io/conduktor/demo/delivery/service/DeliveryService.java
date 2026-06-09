package io.conduktor.demo.delivery.service;

import io.conduktor.demo.delivery.event.DeliveryStatusEvent;
import io.conduktor.demo.delivery.event.OrderEvent;
import io.conduktor.demo.delivery.kafka.DeliveryStatusProducer;
import io.conduktor.demo.delivery.model.DeliveryAssignment;
import io.conduktor.demo.delivery.model.DeliveryStatus;
import io.conduktor.demo.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryStatusProducer deliveryStatusProducer;

    // default delivery persons for NOW
    private static final List<String[]> DELIVERY_PERSONS = Arrays.asList(
            new String[]{"Ravi Kumar", "+91-9876543210"},
            new String[]{"Priya Singh", "+91-9876543211"},
            new String[]{"Amit Sharma", "+91-9876543212"}
    );

    public DeliveryAssignment assignDelivery(OrderEvent event) {
        // Pick a random delivery person
        String[] person = DELIVERY_PERSONS.get(new Random().nextInt(DELIVERY_PERSONS.size()));

        DeliveryAssignment assignment = new DeliveryAssignment();
        assignment.setOrderId(event.getOrderId());
        assignment.setCustomerId(event.getCustomerId());
        assignment.setDeliveryAddress(event.getDeliveryAddress());
        assignment.setDeliveryPersonName(person[0]);
        assignment.setDeliveryPersonPhone(person[1]);
        assignment.setStatus(DeliveryStatus.ASSIGNED);
        assignment.setCurrentLocation("Restaurant");

        DeliveryAssignment saved = deliveryRepository.save(assignment);

        // Publish ASSIGNED status to Kafka  for the Notification Service consumes
        deliveryStatusProducer.publishStatus(DeliveryStatusEvent.builder()
                .orderId(event.getOrderId())
                .customerId(event.getCustomerId())
                .status("ASSIGNED")
                .deliveryPersonName(person[0])
                .currentLocation("Restaurant")
                .message("Your delivery has been assigned to " + person[0])
                .timestamp(LocalDateTime.now())
                .build());

        log.info("Delivery assigned for orderId: {}", event.getOrderId());
        return saved;
    }

    public DeliveryAssignment updateStatus(Long orderId, DeliveryStatus newStatus, String location) {
        DeliveryAssignment assignment = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found for order: " + orderId));

        assignment.setStatus(newStatus);
        assignment.setCurrentLocation(location);

        if (newStatus == DeliveryStatus.PICKED_UP) assignment.setPickedUpAt(LocalDateTime.now());
        if (newStatus == DeliveryStatus.DELIVERED) assignment.setDeliveredAt(LocalDateTime.now());

        DeliveryAssignment saved = deliveryRepository.save(assignment);

        // Publish status update to Kafka
        deliveryStatusProducer.publishStatus(DeliveryStatusEvent.builder()
                .orderId(orderId)
                .customerId(assignment.getCustomerId())
                .status(newStatus.name())
                .deliveryPersonName(assignment.getDeliveryPersonName())
                .currentLocation(location)
                .message(buildStatusMessage(newStatus, assignment.getDeliveryPersonName()))
                .timestamp(LocalDateTime.now())
                .build());

        return saved;
    }

    public void cancelDelivery(Long orderId) {
        deliveryRepository.findByOrderId(orderId).ifPresent(assignment -> {
            assignment.setStatus(DeliveryStatus.FAILED);
            deliveryRepository.save(assignment);
            log.info("Delivery cancelled for orderId: {}", orderId);
        });
    }

    public Optional<DeliveryAssignment> getDeliveryByOrderId(Long orderId) {
        return deliveryRepository.findByOrderId(orderId);
    }

    private String buildStatusMessage(DeliveryStatus status, String personName) {
        return switch (status) {
            case PICKED_UP  -> personName + " has picked up your order";
            case IN_TRANSIT -> "Your order is on the way!";
            case DELIVERED  -> "Order delivered! Enjoy your meal!";
            case FAILED     -> "Delivery failed. Please contact support.";
            default         -> "Status updated to " + status;
        };
    }
}
