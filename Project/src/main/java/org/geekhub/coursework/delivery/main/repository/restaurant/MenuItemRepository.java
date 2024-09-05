package org.geekhub.coursework.delivery.main.repository.restaurant;

import org.geekhub.coursework.delivery.main.model.MenuItem;

import java.util.List;

public interface MenuItemRepository {

    List<MenuItem> findByRestaurantId(Long restaurantId);

    void save(MenuItem menuItem);

    void delete(long id);

    void update(MenuItem menuItem);

    MenuItem findById(Long id);

}
