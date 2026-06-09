package io.conduktor.demo.delivery.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long menuItemId;
    private String itemName;
    private Integer quantity;
    private Double price;
}
