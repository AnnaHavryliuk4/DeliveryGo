package org.geekhub.coursework.delivery.main.repository.restaurant;

import org.geekhub.coursework.delivery.main.model.Restaurant;
import org.springframework.jdbc.core.RowMapper;

public class RestaurantMapper {
    private RestaurantMapper(){
    }

    static RowMapper<Restaurant> getRestaurantRowMapper() {
        return (rs, i) -> {
            Restaurant restaurant = new Restaurant();
            restaurant.setId(rs.getLong("id"));
            restaurant.setName(rs.getString("name"));
            restaurant.setDeliveryMethod(rs.getString("delivery_method"));

            return restaurant;
        };
    }
}
