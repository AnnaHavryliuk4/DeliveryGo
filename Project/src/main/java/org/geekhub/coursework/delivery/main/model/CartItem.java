package org.geekhub.coursework.delivery.main.model;

import java.math.BigDecimal;

public class CartItem {
    private Long id;
    private MenuItem menuItem;
    private Restaurant restaurant;
    private int quantity;
    private BigDecimal totalPrice;
    private Long userId;

    public CartItem() {
        this.menuItem = new MenuItem();
        this.restaurant = new Restaurant();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMenuName() {
        return menuItem.getName();
    }

    public BigDecimal getMenuPrice() {
        return menuItem.getPrice();
    }

    public void setMenuName(String name) {
        this.menuItem.setName(name);
    }

    public void setMenuPrice(BigDecimal price) {
        this.menuItem.setPrice(price);
    }

    public Long getMenuId() {return menuItem.getId();}

    public void setMenuId(Long id) {this.menuItem.setId(id);}

    public String getRestaurantName() {return restaurant.getName();}

    public Long getRestaurantId() {
        return restaurant.getId();
    }

    public void setRestaurantName(String name) {
        this.restaurant.setName(name);
    }

    public void setRestaurantId(Long id) {
        this.restaurant.setId(id);
    }

    public int getQuantity() {
        this.quantity = 1;
        return quantity;
    }

    public void setQuantity(int quantity) {this.quantity = quantity;}

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setRestaurantDeliveryMethod(String deliveryMethod) {this.restaurant.setDeliveryMethod(deliveryMethod);}

    public String getRestaurantDeliveryMethod() {
        return restaurant.getDeliveryMethod();
    }
}
