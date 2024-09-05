package org.geekhub.coursework.delivery.main.repository.restaurant;

import org.geekhub.coursework.delivery.main.model.Restaurant;

import java.util.List;

public interface RestaurantsRepository {

    void save(Restaurant restaurant);

    void delete(long id);

    void update(Restaurant restaurant);

    List<Restaurant> findAll();
}
