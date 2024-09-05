package org.geekhub.coursework.delivery.main.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.geekhub.coursework.delivery.authorization.service.UserService;
import org.geekhub.coursework.delivery.main.service.order.OrderService;
import org.geekhub.coursework.delivery.main.model.CartItem;
import org.geekhub.coursework.delivery.main.model.Order;
import org.geekhub.coursework.delivery.main.service.cart.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class OrderController {
    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;

    public OrderController(OrderService orderService, CartService cartService, UserService userService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/order")
    public String showOrderPage(Model model, HttpServletRequest request) {
        List<CartItem> cartItems = cartService.getAllItemsInCart();
        Order order = new Order();

        BigDecimal check = cartService.calculateTotalPrice(cartItems);

        LocalTime receiptTime = orderService.calculateReceiptTime(LocalTime.now());

        String deliveryMethod = orderService.determineDeliveryMethod(cartItems);

        BigDecimal deliveryPrice = orderService.calculateDeliveryPrice(cartItems);

        BigDecimal totalPrice = orderService.calculateTotalPriceWithDelivery(cartItems);

        String formattedReceiptTime = receiptTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        model.addAttribute("formattedReceiptTime", formattedReceiptTime);
        model.addAttribute("deliveryMethod", deliveryMethod);
        model.addAttribute("check", check);
        model.addAttribute("deliveryPrice", deliveryPrice);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("order", order);

        return "order_page";
    }

    @PostMapping("/order/create")
    public String createOrder(@RequestParam String deliveryMethod,
                              @RequestParam String address,
                              @RequestParam String addressNotes,
                              @RequestParam BigDecimal shippingCost,
                              @RequestParam String receiptTime,
                              @RequestParam String customerName,
                              @RequestParam String phoneNumber,
                              @RequestParam String notes,
                              @RequestParam String paymentMethod) {
        Long userId = userService.getUserId();

        List<CartItem> cartItems = cartService.getCartItemsByUserId(userId);

        LocalTime parsedReceiptTime = LocalTime.parse(receiptTime);
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), parsedReceiptTime);

        Order order = new Order();
        order.setDeliveryMethod(deliveryMethod);
        order.setAddress(address);
        order.setAddressNotes(addressNotes);
        order.setShippingCost(shippingCost);
        order.setReceiptTime(dateTime);
        order.setTime(LocalDateTime.now());
        order.setCustomerName(customerName);
        order.setPhoneNumber(phoneNumber);
        order.setNotes(notes);
        order.setPaymentMethod(paymentMethod);
        order.setUserId(userId);
        order.setDeliveryMethod(deliveryMethod);

        orderService.addOrder(order, userId);
        orderService.addOrderItems(cartItems, order);
        cartService.clearCartByUserId(userId);

        return "redirect:/order-confirmation";
    }

    @GetMapping("/order-confirmation")
    public String showOrderConfirmationPage() {
        return "order_confirmation";
    }


    @GetMapping("/user_orders/{userId}")
    public String getUserOrders(@PathVariable long userId, Model model) {
        List<Order> orders = orderService.getOrdersForUser(userId);
        List<Order> orderItems = orderService.getOrdersItemForUser(orders);

        model.addAttribute("orders", orders);
        model.addAttribute("cart", orderItems);

        return "user_orders";
    }
}
