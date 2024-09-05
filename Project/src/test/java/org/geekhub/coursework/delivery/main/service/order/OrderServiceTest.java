package org.geekhub.coursework.delivery.main.service.order;

import org.geekhub.coursework.delivery.main.exceptions.OrderException;
import org.geekhub.coursework.delivery.main.model.CartItem;
import org.geekhub.coursework.delivery.main.model.Order;
import org.geekhub.coursework.delivery.main.repository.cart.CartRepositoryImpl;
import org.geekhub.coursework.delivery.main.repository.order.OrderRepository;
import org.geekhub.coursework.delivery.main.service.cart.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepositoryImpl cartRepository;
    private CartService cartService;
    private OrderService orderService;
    private static final int DELIVERY_PRICE = 70;

    @BeforeEach
    void setUp() {
        cartService = new CartService(cartRepository);
        orderService = new OrderService(orderRepository, cartService);
    }

    @Test
    void addOrder_whenCartIsNotEmpty_shouldAddOrder() {
        List<CartItem> cartItems = List.of(new CartItem());
        when(cartService.getCartItemsByUserId(anyLong())).thenReturn(cartItems);
        Long userId = 1L;
        Order order = new Order();

        orderService.addOrder(order, userId);
        verify(orderRepository, times(1)).addOrder(order);
    }

    @Test
    void addOrder_whenCartWithNoItems_shouldThrowOrderException() {
        Long userId = 1L;
        Order emptyOrder = new Order();

        OrderException exception = assertThrows(OrderException.class, () -> orderService.addOrder(emptyOrder, userId));
        assertEquals("Failed to add order: No items added to the order", exception.getMessage());

        verify(orderRepository, never()).addOrder(any());
    }

    @Test
    void addOrder_shouldReturnExceptionThrow() {
        List<CartItem> cartItems = List.of(new CartItem());
        when(cartService.getCartItemsByUserId(anyLong())).thenReturn(cartItems);

        Long userId = 1L;
        Order order = new Order();
        doThrow(new RuntimeException("Database error")).when(orderRepository).addOrder(order);

        OrderException exception = assertThrows(OrderException.class, () -> orderService.addOrder(order, userId));
        assertEquals("Failed to add order: Database error", exception.getMessage());

        verify(orderRepository, times(1)).addOrder(order);
    }

    @Test
    void addOrderItems_shouldAddNewItemToUserOrder() {
        List<CartItem> cartItems = new ArrayList<>();
        Order order = new Order();

        orderService.addOrderItems(cartItems, order);

        ArgumentCaptor<List<CartItem>> cartItemCaptor = forClass(List.class);
        ArgumentCaptor<Order> orderCaptor = forClass(Order.class);
        verify(orderRepository, times(1)).addOrderItems(cartItemCaptor.capture(), orderCaptor.capture());

        assertEquals(cartItems, cartItemCaptor.getValue());
        assertEquals(order, orderCaptor.getValue());
    }

    @Test
    void addOrderItems_ReturnExceptionThrow() {
        List<CartItem> cartItems = new ArrayList<>();
        Order order = new Order();
        doThrow(new RuntimeException("Database error")).when(orderRepository).addOrderItems(cartItems, order);

        OrderException exception = assertThrows(OrderException.class, () -> orderService.addOrderItems(cartItems, order));
        assertEquals("Failed to add order items: Database error", exception.getMessage());

        verify(orderRepository, times(1)).addOrderItems(cartItems, order);
    }

    @Test
    void getOrdersForUser_shouldReturnOrdersByUserId() {
        long userId = 1L;
        List<Order> orders = List.of(new Order());
        when(orderRepository.getOrdersForUser(userId)).thenReturn(orders);

        List<Order> result = orderService.getOrdersForUser(1L);
        assertEquals(orders, result);
    }

    @Test
    void getOrdersForUser_returnExceptionThrow() {
        long userId = 1L;
        when(orderRepository.getOrdersForUser(userId)).thenThrow(new RuntimeException("Database error"));

        OrderException exception = assertThrows(OrderException.class, () -> orderService.getOrdersForUser(userId));
        assertEquals("Failed to get order for user by id = " + userId + ":Database error", exception.getMessage());
    }


    @Test
    void getOrdersItemForUser_shouldReturnListWithOrderItemForUser() {
        List<Order> expectedOrders = new ArrayList<>();
        expectedOrders.add(new Order());
        when(orderRepository.getOrdersItemForUser(anyList())).thenReturn(expectedOrders);

        List<Order> actualOrders = orderService.getOrdersItemForUser(expectedOrders);
        assertEquals(expectedOrders, actualOrders);

        verify(orderRepository, times(1)).getOrdersItemForUser(expectedOrders);
    }

    @Test
    void getOrdersItemForUser_returnExceptionThrow() {
        List<Order> orders = new ArrayList<>();
        when(orderRepository.getOrdersItemForUser(anyList())).thenThrow(new RuntimeException("Database error"));

        OrderException exception = assertThrows(OrderException.class, () -> orderService.getOrdersItemForUser(orders));
        assertEquals("Failed to get order items for user: Database error", exception.getMessage());
        verify(orderRepository, times(1)).getOrdersItemForUser(orders);
    }

    @Test
    void calculateDeliveryPrice_whenDeliveryIsNotPresent_ReturnsZero() {
        CartItem cartItem = new CartItem();
        cartItem.setRestaurantDeliveryMethod("takeout");
        List<CartItem> cartItems = List.of(cartItem);

        BigDecimal result = orderService.calculateDeliveryPrice(cartItems);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void calculateDeliveryPrice_whenDeliveryIsPresent_shouldReturnDeliveryPrice() {
        CartItem cartItem = new CartItem();
        cartItem.setRestaurantDeliveryMethod("delivery");
        List<CartItem> cartItems = List.of(cartItem);

        BigDecimal result = orderService.calculateDeliveryPrice(cartItems);
        assertEquals(BigDecimal.valueOf(DELIVERY_PRICE), result);
    }

    @Test
    void calculateDeliveryPrice_whenEmptyList_ReturnsZero() {
        List<CartItem> cartItems = List.of();

        BigDecimal result = orderService.calculateDeliveryPrice(cartItems);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void calculateDeliveryPrice_whenDeliveryMethodIsTakeout_ReturnsZero() {
        CartItem cartItem1 = new CartItem();
        cartItem1.setTotalPrice(BigDecimal.valueOf(50));
        cartItem1.setRestaurantDeliveryMethod("takeout");

        List<CartItem> cartItems = List.of(cartItem1);

        BigDecimal result = orderService.calculateDeliveryPrice(cartItems);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void calculateTotalPriceWithDelivery_shouldCallsCalculateTotalPriceInCartService() {
        List<CartItem> cartItems = Collections.singletonList(new CartItem());
        when(cartService.calculateTotalPrice(cartItems)).thenReturn(BigDecimal.TEN);

        BigDecimal totalPriceWithDelivery = orderService.calculateTotalPriceWithDelivery(cartItems);
        verify(cartService).calculateTotalPrice(cartItems);
    }

    @Test
    void calculateTotalPriceWithDelivery_NoDeliveryItems_ReturnsTotalPrice() {
        CartItem cartItem1 = new CartItem();
        cartItem1.setTotalPrice(BigDecimal.valueOf(50));

        List<CartItem> cartItems = List.of(cartItem1);

        BigDecimal totalPrice = cartService.calculateTotalPrice(cartItems);
        BigDecimal result = orderService.calculateTotalPriceWithDelivery(cartItems);
        assertEquals(totalPrice, result);
    }

    @Test
    void calculateTotalPriceWithDelivery_WithDeliveryItems_ReturnsTotalPriceWithDelivery() {
        CartItem cartItem1 = new CartItem();

        cartItem1.setTotalPrice(BigDecimal.valueOf(50));
        cartItem1.setRestaurantDeliveryMethod("delivery");

        List<CartItem> cartItems = List.of(cartItem1);

        BigDecimal totalPrice = cartService.calculateTotalPrice(cartItems);
        BigDecimal expectedTotalPriceWithDeliveryPrice = totalPrice.add(BigDecimal.valueOf(DELIVERY_PRICE));
        BigDecimal result = orderService.calculateTotalPriceWithDelivery(cartItems);
        assertEquals(expectedTotalPriceWithDeliveryPrice, result);
    }

    @Test
    void determineDeliveryMethod_whenWithDeliveryMethod_shouldReturnDelivery() {
        List<CartItem> cartItems = new ArrayList<>();
        CartItem deliveryItem = new CartItem();
        deliveryItem.setRestaurantDeliveryMethod("delivery");
        cartItems.add(deliveryItem);

        String deliveryMethod = orderService.determineDeliveryMethod(cartItems);
        assertEquals("delivery", deliveryMethod);
    }

    @Test
    void determineDeliveryMethod_whenWithTakeoutItem_shouldReturnTakeout() {
        List<CartItem> cartItems = new ArrayList<>();
        CartItem takeoutItem = new CartItem();
        takeoutItem.setRestaurantDeliveryMethod("takeout");
        cartItems.add(takeoutItem);

        String deliveryMethod = orderService.determineDeliveryMethod(cartItems);
        assertEquals("takeout", deliveryMethod);
    }

    @Test
    void calculateReceiptTime_whenTimeBefore8AM_shouldReturn10AM() {
        LocalTime currentTime = LocalTime.of(7, 59);
        LocalTime receiptTime = orderService.calculateReceiptTime(currentTime);
        assertEquals(LocalTime.of(10, 0), receiptTime);
    }

    @Test
    void calculateReceiptTime_whenTimeAfter10PM_shouldReturn10AM() {
        LocalTime currentTime = LocalTime.of(22, 1);
        LocalTime receiptTime = orderService.calculateReceiptTime(currentTime);
        assertEquals(LocalTime.of(10, 0), receiptTime);
    }

    @Test
    void calculateReceiptTime_whenTimeBetween8AMAnd10PM_shouldReturnCurrentTimePlus2Hours() {
        LocalTime currentTime = LocalTime.of(12, 0);
        LocalTime receiptTime = orderService.calculateReceiptTime(currentTime);
        assertEquals(currentTime.plusHours(2), receiptTime);
    }
}
