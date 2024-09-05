package org.geekhub.coursework.delivery.main.repository.cart;


import org.geekhub.coursework.delivery.main.model.CartItem;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

import static org.geekhub.coursework.delivery.main.repository.cart.CartMapper.getCartItemRowMapper;

@Repository
public class CartRepositoryImpl implements CartRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CartRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<CartItem> getAllItemsInCart() {
        String query = "SELECT * FROM cart";

        return jdbcTemplate.query(query, getCartItemRowMapper());
    }

    @Override
    public CartItem findByIdMenu(Long menuId) {
        String query = "SELECT * FROM cart WHERE item_id = :item_id";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("item_id", menuId);

        List<CartItem> cartItems = jdbcTemplate.query(query, parameters, getCartItemRowMapper());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Item with ID " + menuId + " not found");
        }

        return cartItems.get(0);
    }

    @Override
    public void removeItemFromCart(Long id) {
        String query = "DELETE FROM cart WHERE id = :id";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        jdbcTemplate.update(query, parameters);
    }

    @Override
    public void addItemToCart(CartItem cartItem) {
        String query = "INSERT INTO cart (restaurant_id, restaurant_name, item_id, item_name, item_price, user_id, quantity, total_price, delivery_method)" +
            "VALUES (:restaurantId, :restaurantName, :menuId, :menuName, :menuPrice, :userId, :quantity, :totalPrice, :deliveryMethod)";

        BigDecimal totalPrice = cartItem.getMenuPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("restaurantId", cartItem.getRestaurantId());
        parameters.addValue("restaurantName", cartItem.getRestaurantName());
        parameters.addValue("menuId", cartItem.getMenuId());
        parameters.addValue("menuName", cartItem.getMenuName());
        parameters.addValue("menuPrice", cartItem.getMenuPrice());
        parameters.addValue("userId", cartItem.getUserId());
        parameters.addValue("quantity", cartItem.getQuantity());
        parameters.addValue("totalPrice", totalPrice);
        parameters.addValue("deliveryMethod", cartItem.getRestaurantDeliveryMethod());

        jdbcTemplate.update(query, parameters);
    }

    @Override
    public void update(CartItem cartItem) {
        String query = "UPDATE cart " +
            "SET restaurant_id = :restaurantId, restaurant_name = :restaurantName, item_id = :menuId, item_name = :menuName, " +
            "item_price = :menuPrice, user_id = :userId, quantity = :quantity, total_price = :totalPrice,delivery_method = :deliveryMethod " +
            "WHERE id = :id";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", cartItem.getId());
        parameters.addValue("restaurantId", cartItem.getRestaurantId());
        parameters.addValue("restaurantName", cartItem.getRestaurantName());
        parameters.addValue("menuId", cartItem.getMenuId());
        parameters.addValue("menuName", cartItem.getMenuName());
        parameters.addValue("menuPrice", cartItem.getMenuPrice());
        parameters.addValue("userId", cartItem.getUserId());
        parameters.addValue("quantity", cartItem.getQuantity());
        parameters.addValue("totalPrice", cartItem.getTotalPrice());
        parameters.addValue("deliveryMethod", cartItem.getRestaurantDeliveryMethod());

        jdbcTemplate.update(query, parameters);
    }


    @Override
    public void clearCart(Long userId) {
        String query = "DELETE FROM cart WHERE user_id = :userId";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);

        jdbcTemplate.update(query, parameters);
    }

    public List<CartItem> getCartItemsByUserId(Long userId) {
        String query = "SELECT * FROM cart WHERE user_id = :userId";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);

        return jdbcTemplate.query(query, parameters, getCartItemRowMapper());
    }

}
