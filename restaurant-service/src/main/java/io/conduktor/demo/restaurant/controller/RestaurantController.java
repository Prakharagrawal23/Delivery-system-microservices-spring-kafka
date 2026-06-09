package io.conduktor.demo.restaurant.controller;

import io.conduktor.demo.restaurant.model.MenuItem;
import io.conduktor.demo.restaurant.model.Restaurant;
import io.conduktor.demo.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
        return ResponseEntity.ok(restaurantService.createRestaurant(restaurant));
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAvailableRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllAvailableRestaurants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurant(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }

    @PutMapping("/{id}/toggle-availability")
    public ResponseEntity<Restaurant> toggleAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.toggleAvailability(id));
    }

    @PostMapping("/{restaurantId}/menu")
    public ResponseEntity<MenuItem> addMenuItem(
            @PathVariable Long restaurantId,
            @RequestBody MenuItem item) {
        return ResponseEntity.ok(restaurantService.addMenuItem(restaurantId, item));
    }

    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<MenuItem>> getMenu(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantService.getMenuByRestaurant(restaurantId));
    }

    // Internal endpoint called by Order Service
    @GetMapping("/menu-items/{itemId}/available")
    public ResponseEntity<Boolean> isAvailable(@PathVariable Long itemId) {
        return ResponseEntity.ok(restaurantService.isMenuItemAvailable(itemId));
    }
}
