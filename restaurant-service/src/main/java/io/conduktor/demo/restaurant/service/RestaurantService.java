package io.conduktor.demo.restaurant.service;

import io.conduktor.demo.restaurant.model.MenuItem;
import io.conduktor.demo.restaurant.model.Restaurant;
import io.conduktor.demo.restaurant.repository.MenuItemRepository;
import io.conduktor.demo.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> getAllAvailableRestaurants() {
        return restaurantRepository.findByAvailableTrue();
    }

    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found: " + id));
    }

    public Restaurant toggleAvailability(Long id) {
        Restaurant r = getRestaurantById(id);
        r.setAvailable(!r.isAvailable());
        return restaurantRepository.save(r);
    }

    public MenuItem addMenuItem(Long restaurantId, MenuItem item) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        item.setRestaurant(restaurant);
        return menuItemRepository.save(item);
    }

    public List<MenuItem> getMenuByRestaurant(Long restaurantId) {
        return menuItemRepository.findByRestaurantIdAndAvailableTrue(restaurantId);
    }

    // Called by Order Service (via REST) to validate item exists and is available
    public boolean isMenuItemAvailable(Long itemId) {
        return menuItemRepository.findById(itemId)
                .map(MenuItem::isAvailable)
                .orElse(false);
    }
}
