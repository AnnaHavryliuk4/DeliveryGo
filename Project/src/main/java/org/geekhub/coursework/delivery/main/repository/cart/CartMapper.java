package org.geekhub.coursework.delivery.main.repository.cart;

import org.geekhub.coursework.delivery.main.model.CartItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CartMapper {

    private CartMapper() {
    }

    static RowMapper<CartItem> getCartItemRowMapper() {
        return (rs, rowNum) -> {
            CartItem cartItem = new CartItem();
            cartItem.setId(rs.getLong("id"));
            cartItem.setMenuId(rs.getLong("item_id"));
            cartItem.setMenuName(rs.getString("item_name"));
            cartItem.setMenuPrice(rs.getBigDecimal("item_price"));
            cartItem.setUserId(rs.getLong("user_id"));
            cartItem.setQuantity(rs.getInt("quantity"));
            cartItem.setRestaurantId(rs.getLong("restaurant_id"));
            cartItem.setRestaurantName(rs.getString("restaurant_name"));
            cartItem.setTotalPrice(rs.getBigDecimal("total_price"));
            cartItem.setRestaurantDeliveryMethod(rs.getString("delivery_method"));
            return cartItem;
        };
    }
}
