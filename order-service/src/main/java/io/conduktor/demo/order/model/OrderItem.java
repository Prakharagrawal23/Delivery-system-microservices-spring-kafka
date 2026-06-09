package io.conduktor.demo.order.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class OrderItem {
    private Long menuItemId;
    private String itemName;
    private Integer quantity;
    private Double price;
}
