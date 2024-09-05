package org.geekhub.coursework.delivery.main.repository.restaurant;

import org.geekhub.coursework.delivery.main.model.Restaurant;
import org.geekhub.coursework.delivery.main.repository.restaurant.RestaurantsRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.geekhub.coursework.delivery.main.repository.restaurant.RestaurantMapper.getRestaurantRowMapper;

@Repository
public class RestaurantsRepositoryImp implements RestaurantsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RestaurantsRepositoryImp(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Restaurant restaurant) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("name", restaurant.getName());
        parameters.addValue("deliveryMethod", restaurant.getDeliveryMethod());

        jdbcTemplate.update("INSERT INTO restaurants (name, delivery_method) VALUES (:name, :deliveryMethod)", parameters);
    }

    @Override
    public void delete(long id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        jdbcTemplate.update("DELETE FROM restaurants WHERE id = :id", parameters);
    }

    @Override
    public void update(Restaurant restaurant) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", restaurant.getId());
        parameters.addValue("name", restaurant.getName());
        parameters.addValue("deliveryMethod", restaurant.getDeliveryMethod());

        jdbcTemplate.update("UPDATE restaurants SET name = :name, delivery_method = :deliveryMethod WHERE id = :id", parameters);
    }

    @Override
    public List<Restaurant> findAll() {
        String query = "SELECT * FROM restaurants";

        return jdbcTemplate.query(query, getRestaurantRowMapper());
    }

    public Restaurant findById(Long id) {
        String query = "SELECT * FROM restaurants WHERE id = :id";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        return jdbcTemplate.queryForObject(query, parameters, getRestaurantRowMapper());
    }

    public List<Restaurant> findAllRestaurantsWithDelivery() {
        String query = "SELECT * FROM restaurants WHERE delivery_method = 'delivery'";

        return jdbcTemplate.query(query, getRestaurantRowMapper());
    }

    public List<Restaurant> findAllRestaurantsWithTakeout() {
        String query = "SELECT * FROM restaurants WHERE delivery_method = 'takeout'";

        return jdbcTemplate.query(query, getRestaurantRowMapper());
    }
}
