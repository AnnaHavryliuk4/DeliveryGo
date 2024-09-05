package org.geekhub.coursework.delivery.main.repository.restaurant;

import org.geekhub.coursework.delivery.main.model.MenuItem;
import org.geekhub.coursework.delivery.main.repository.restaurant.MenuItemRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.geekhub.coursework.delivery.main.repository.restaurant.MenuItemMapper.getMenuRowMapper;

@Repository
public class MenuItemRepositoryImpl implements MenuItemRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public MenuItemRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MenuItem> findByRestaurantId(Long restaurantId) {
        String query = "SELECT * FROM menu_items WHERE restaurant_id = :restaurantId";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("restaurantId", restaurantId);

        return jdbcTemplate.query(query, parameters, getMenuRowMapper());
    }

    @Override
    public void save(MenuItem menuItem) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("name", menuItem.getName());
        parameters.addValue("price", menuItem.getPrice());
        parameters.addValue("restaurant_id", menuItem.getRestaurantId());
        parameters.addValue("weight", menuItem.getWeight());
        parameters.addValue("serving", menuItem.getServing());
        parameters.addValue("description", menuItem.getDescription());

        jdbcTemplate.update("INSERT INTO menu_items (name,price,restaurant_id,weight,serving,description) VALUES (:name, :price,:restaurant_id, :weight,:serving,:description)", parameters);
    }

    @Override
    public void delete(long id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        jdbcTemplate.update("DELETE FROM menu_items WHERE id = :id", parameters);
    }

    @Override
    public void update(MenuItem menuItem) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", menuItem.getId());
        parameters.addValue("name", menuItem.getName());
        parameters.addValue("price", menuItem.getPrice());
        parameters.addValue("weight", menuItem.getWeight());
        parameters.addValue("serving", menuItem.getServing());
        parameters.addValue("description", menuItem.getDescription());

        jdbcTemplate.update("UPDATE menu_items SET name = :name, price = :price, weight=:weight, serving=:serving, description=:description WHERE id = :id", parameters);
    }

    @Override
    public MenuItem findById(Long id) {
        String query = "SELECT * FROM menu_items WHERE id = :id";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        return jdbcTemplate.queryForObject(query, parameters, getMenuRowMapper());
    }

}
