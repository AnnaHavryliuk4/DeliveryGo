package org.geekhub.coursework.delivery.main.service.order;

import org.geekhub.coursework.delivery.main.exceptions.OrderException;
import org.geekhub.coursework.delivery.main.repository.order.OrderRepository;
import org.geekhub.coursework.delivery.main.model.CartItem;
import org.geekhub.coursework.delivery.main.model.Order;
import org.geekhub.coursework.delivery.main.service.cart.CartService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private static final int DELIVERY_PRICE = 70;

    public OrderService(OrderRepository orderRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
    }

    public void addOrder(Order order,Long userId) {
        if (cartService.getCartItemsByUserId(userId).isEmpty()) {
            throw new OrderException("Failed to add order: No items added to the order");
        }

        try {
            orderRepository.addOrder(order);
        } catch (Exception e) {
            throw new OrderException("Failed to add order: " + e.getMessage());
        }
    }


    public void addOrderItems(List<CartItem> cartItem, Order order) {
        try {
            orderRepository.addOrderItems(cartItem, order);
        } catch (Exception e) {
            throw new OrderException("Failed to add order items: " + e.getMessage());
        }
    }

    public List<Order> getOrdersForUser(Long userId) {
        try {
            return orderRepository.getOrdersForUser(userId);
        } catch (Exception e) {
            throw new OrderException("Failed to get order for user by id = " + userId + ":" + e.getMessage());
        }
    }

    public List<Order> getOrdersItemForUser(List<Order> orders) {
        try {
            return orderRepository.getOrdersItemForUser(orders);
        } catch (Exception e) {
            throw new OrderException("Failed to get order items for user: " + e.getMessage());
        }
    }

    public BigDecimal calculateDeliveryPrice(List<CartItem> cartItems) {
        BigDecimal deliveryPrice = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            if (cartItem.getRestaurantDeliveryMethod() != null && cartItem.getRestaurantDeliveryMethod().equals("delivery")) {
                deliveryPrice = deliveryPrice.add(BigDecimal.valueOf(DELIVERY_PRICE));
                break;
            }
        }

        return deliveryPrice;
    }

    public BigDecimal calculateTotalPriceWithDelivery(List<CartItem> cartItems) {
        BigDecimal totalPrice = cartService.calculateTotalPrice(cartItems);
        BigDecimal deliveryPrice = calculateDeliveryPrice(cartItems);

        return totalPrice.add(deliveryPrice);
    }

    public String determineDeliveryMethod(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getRestaurantDeliveryMethod() != null && cartItem.getRestaurantDeliveryMethod().equals("delivery")) {
                return "delivery";
            }
        }

        return "takeout";
    }

    public LocalTime calculateReceiptTime(LocalTime currentTime) {
        LocalTime deliveryTime;

        if (currentTime.isAfter(LocalTime.of(22, 0))) {
            deliveryTime = LocalTime.of(10, 0);
        } else if (currentTime.isBefore(LocalTime.of(8, 0))) {
            deliveryTime = LocalTime.of(10, 0);
        } else {
            deliveryTime = currentTime.plusHours(2);
        }

        return deliveryTime;
    }
}
