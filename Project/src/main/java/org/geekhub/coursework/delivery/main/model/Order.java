package org.geekhub.coursework.delivery.main.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private Long id;
    private String address;
    private String addressNotes;
    private BigDecimal shippingCost;
    private LocalDateTime receiptTime;
    private LocalDateTime time;
    private String customerName;
    private String phoneNumber;
    private String notes;
    private String paymentMethod;
    private String deliveryMethod;
    private Long userId;
    private final MenuItem menuItem;
    private final Restaurant restaurant;
    private List<Order> orderItems;

    public Order() {
        this.menuItem = new MenuItem();
        this.restaurant = new Restaurant();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressNotes() {
        return addressNotes;
    }

    public void setAddressNotes(String addressNotes) {
        this.addressNotes = addressNotes;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public LocalDateTime getReceiptTime() {
        return receiptTime;
    }

    public void setReceiptTime(LocalDateTime receiptTime) {
        this.receiptTime = receiptTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getRestaurantName(){
        return this.restaurant.getName();
    }

    public void setRestaurantName(String name){
        this.restaurant.setName(name);
    }

    public String getMenuName(){
        return this.menuItem.getName();
    }

    public void setMenuQuantity(int quantity){
        this.menuItem.setQuantity(quantity);
    }

    public int getMenuQuantity(){
        return this.menuItem.getQuantity();
    }

    public void setMenuName(String name){
        this.menuItem.setName(name);
    }

    public List<Order> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<Order> orderItems) {
        this.orderItems = orderItems;
    }
}
