package org.geekhub.coursework.delivery.main.repository.order;

import org.geekhub.coursework.delivery.main.model.CartItem;
import org.geekhub.coursework.delivery.main.model.Order;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import static org.geekhub.coursework.delivery.main.repository.order.OrderMapper.getOrderItemRowMapper;
import static org.geekhub.coursework.delivery.main.repository.order.OrderMapper.getOrderRowMapper;

@Repository
public class OrderRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public OrderRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addOrder(Order order) {
        String query = "INSERT INTO orders " +
            "(delivery_method," +
            " address," +
            " address_notes," +
            " shipping_cost," +
            " receipt_time," +
            " time," +
            " customer_name," +
            " phone_number," +
            " notes," +
            " payment_method," +
            " user_id)" +
            " VALUES" +
            " (:deliveryMethod," +
            " :address," +
            " :addressNotes," +
            " :shippingCost," +
            " :receiptTime," +
            " :time,"+
            " :customerName," +
            " :phoneNumber," +
            " :notes," +
            " :paymentMethod," +
            " :userId)";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("deliveryMethod", order.getDeliveryMethod());
        parameters.addValue("address", order.getAddress());
        parameters.addValue("addressNotes", order.getAddressNotes());
        parameters.addValue("shippingCost", order.getShippingCost());
        parameters.addValue("receiptTime", order.getReceiptTime());
        parameters.addValue("time", order.getTime());
        parameters.addValue("customerName", order.getCustomerName());
        parameters.addValue("phoneNumber", order.getPhoneNumber());
        parameters.addValue("notes", order.getNotes());
        parameters.addValue("paymentMethod", order.getPaymentMethod());
        parameters.addValue("userId", order.getUserId());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(query, parameters, keyHolder, new String[]{"id"});

        order.setId(keyHolder.getKey().longValue());
    }

    public void addOrderItems(List<CartItem> items, Order order) {
        String query = "INSERT INTO order_items (order_id, item_id, restaurant_name, menu_item_name,quantity)" +
            "VALUES (:order_id, :menu_item_id, :restaurant_name, :menu_item_name,:quantity)";

        for (CartItem item : items) {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("order_id", order.getId());
            parameters.addValue("menu_item_id", item.getMenuId());
            parameters.addValue("restaurant_name", item.getRestaurantName());
            parameters.addValue("menu_item_name", item.getMenuName());
            parameters.addValue("quantity", item.getQuantity());

            jdbcTemplate.update(query, parameters);
        }
    }
    public List<Order> getOrdersForUser(long userId) {
        String query = "SELECT * FROM orders WHERE user_id = :userId";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);

        return jdbcTemplate.query(query,parameters, getOrderRowMapper());
    }
    public List<Order> getOrdersItemForUser(List<Order> orders) {
        List<Order> orderItems = new ArrayList<>();

        for (Order order : orders) {
            long orderId = order.getId();
            String query = "SELECT * FROM order_items WHERE order_id = :orderId";
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("orderId", orderId);

            List<Order> orderItemList = jdbcTemplate.query(query, parameters, getOrderItemRowMapper());

            order.setOrderItems(orderItemList);
        }

        return orderItems;
    }
}
