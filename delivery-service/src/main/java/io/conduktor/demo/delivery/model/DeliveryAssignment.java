package io.conduktor.demo.delivery.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_assignments")
@Data
public class DeliveryAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long customerId;
    private String deliveryAddress;
    private String deliveryPersonName;
    private String deliveryPersonPhone;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus  status;

    private LocalDateTime assignedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;
    private String currentLocation;

    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
        if (status == null) status = DeliveryStatus.ASSIGNED;
    }
}