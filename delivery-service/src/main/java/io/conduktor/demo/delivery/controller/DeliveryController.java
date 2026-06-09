package io.conduktor.demo.delivery.controller;

import io.conduktor.demo.delivery.model.DeliveryAssignment;
import io.conduktor.demo.delivery.model.DeliveryStatus;
import io.conduktor.demo.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryAssignment> getDeliveryStatus(@PathVariable Long orderId) {
        return deliveryService.getDeliveryByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/order/{orderId}/status")
    public ResponseEntity<DeliveryAssignment> updateStatus(
            @PathVariable Long orderId,
            @RequestParam DeliveryStatus status,
            @RequestParam(defaultValue = "In transit") String location) {
        return ResponseEntity.ok(deliveryService.updateStatus(orderId, status, location));
    }
}
