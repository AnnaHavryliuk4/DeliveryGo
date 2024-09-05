package org.geekhub.coursework.delivery.main.repository.order;

import org.geekhub.coursework.delivery.main.model.Order;
import org.springframework.jdbc.core.RowMapper;

import java.time.ZoneOffset;

public class OrderMapper {

    private OrderMapper(){
    }

    static RowMapper<Order> getOrderItemRowMapper() {
        return (rs, rowNum) -> {
            Order order = new Order();
            order.setMenuName(rs.getString("menu_item_name"));
            order.setRestaurantName(rs.getString("restaurant_name"));
            order.setMenuQuantity(rs.getInt("quantity"));

            return order;
        };
    }

    static RowMapper<Order> getOrderRowMapper() {
        return (rs, rowNum) -> {
            Order order = new Order();
            order.setId(rs.getLong("id"));
            order.setUserId(rs.getLong("user_id"));
            order.setShippingCost(rs.getBigDecimal("shipping_cost"));
            order.setTime(rs.getTimestamp("time").toLocalDateTime());
            order.setReceiptTime(rs.getTimestamp("receipt_time").toLocalDateTime());
            order.setDeliveryMethod(rs.getString("delivery_method"));
            order.setAddress(rs.getString("address"));
            order.setAddressNotes(rs.getString("address_notes"));
            order.setCustomerName(rs.getString("customer_name"));
            order.setPhoneNumber(rs.getString("phone_number"));
            order.setNotes(rs.getString("notes"));
            order.setPaymentMethod(rs.getString("payment_method"));

            return order;
        };
    }
}
