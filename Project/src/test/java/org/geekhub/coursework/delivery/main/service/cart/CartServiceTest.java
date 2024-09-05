package org.geekhub.coursework.delivery.main.service.cart;

import org.geekhub.coursework.delivery.main.exceptions.CartException;
import org.geekhub.coursework.delivery.main.model.CartItem;
import org.geekhub.coursework.delivery.main.repository.cart.CartRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock
    private CartRepositoryImpl cartRepository;
    private CartService cartService;
    private static final double PACKAGING = 4.0;


    @BeforeEach
    void setUp() {
        cartService = new CartService(cartRepository);
    }

    @Test
    void getAllItemsInCart_ShouldReturnAllItems() {
        List<CartItem> expectedItems = new ArrayList<>();
        when(cartRepository.getAllItemsInCart()).thenReturn(expectedItems);

        List<CartItem> actualItems = cartService.getAllItemsInCart();
        assertEquals(expectedItems, actualItems);
    }

    @Test
    void getAllItemsInCart_WhenCartIsEmptyShouldReturnEmptyList() {
        List<CartItem> emptyCartItems = Collections.emptyList();
        when(cartRepository.getAllItemsInCart()).thenReturn(emptyCartItems);

        List<CartItem> actualItems = cartService.getAllItemsInCart();
        assertNotNull(actualItems);
        assertTrue(actualItems.isEmpty());
    }

    @Test
    void getAllItemsInCart_ShouldReturnExceptionThrown() {
        when(cartRepository.getAllItemsInCart()).thenThrow(new RuntimeException("Database error"));

        CartException exception = assertThrows(CartException.class, () -> cartService.getAllItemsInCart());
        assertEquals("Failed get all items from cart: Database error", exception.getMessage());
    }

    @Test
    void removeItemFromCart_ShouldRemoveItemFromCartById() {
        Long itemId = 1L;
        cartService.removeItemFromCart(itemId);
        verify(cartRepository, times(1)).removeItemFromCart(itemId);
    }

    @Test
    void removeItemFromCart_ShouldReturnExceptionThrown() {
        Long itemId = 1L;
        doThrow(new RuntimeException("Database error")).when(cartRepository).removeItemFromCart(itemId);

        CartException exception = assertThrows(CartException.class, () -> cartService.removeItemFromCart(itemId));
        assertEquals("Failed remove item from cart: Database error", exception.getMessage());

        verify(cartRepository, times(1)).removeItemFromCart(itemId);
    }

    @Test
    void clearCartByUserId_ShouldClearCartByUser() {
        Long userId = 1L;
        cartService.clearCartByUserId(userId);
        verify(cartRepository, times(1)).clearCart(userId);
    }

    @Test
    void clearCartByUserId_ShouldReturnExceptionThrown() {
        Long userId = 1L;
        doThrow(new RuntimeException("Database error")).when(cartRepository).clearCart(userId);

        CartException exception = assertThrows(CartException.class, () -> cartService.clearCartByUserId(userId));
        assertEquals("Failed clear cart: Database error", exception.getMessage());

        verify(cartRepository, times(1)).clearCart(userId);
    }

    @Test
    void addItemToCart_ShouldAddNewItemToCart() {
        CartItem cartItem = new CartItem();
        cartService.addItemToCart(cartItem);
        verify(cartRepository, times(1)).addItemToCart(cartItem);
    }

    @Test
    void addItemToCart_ShouldReturnExceptionThrown() {
        CartItem cartItem = new CartItem();
        doThrow(new RuntimeException("Database error")).when(cartRepository).addItemToCart(cartItem);

        CartException exception = assertThrows(CartException.class, () -> cartService.addItemToCart(cartItem));
        assertEquals("Failed to add item to cart: Database error", exception.getMessage());

        verify(cartRepository, times(1)).addItemToCart(cartItem);
    }

    @Test
    void getCartItemsByUserId_ShouldReturnCartWithItemsForUser() {
        Long userId = 1L;
        List<CartItem> expectedItems = new ArrayList<>();

        when(cartRepository.getCartItemsByUserId(userId)).thenReturn(expectedItems);
        List<CartItem> actualItems = cartService.getCartItemsByUserId(userId);

        assertEquals(expectedItems, actualItems);
    }

    @Test
    void calculateTotalPrice_WhenCartIsEmpty_ShouldReturnPackagingPrice() {
        List<CartItem> items = new ArrayList<>();
        BigDecimal totalPrice = cartService.calculateTotalPrice(items);
        assertEquals(BigDecimal.valueOf(PACKAGING), totalPrice);
    }

    @Test
    void calculateTotalPrice_ShouldReturnTotalPriceOfAllItemsInTheCart() {
        List<CartItem> items = new ArrayList<>();
        BigDecimal item1Price = BigDecimal.valueOf(10);
        BigDecimal item2Price = BigDecimal.valueOf(15);
        CartItem item1 = new CartItem();
        item1.setTotalPrice(item1Price);
        CartItem item2 = new CartItem();
        item2.setTotalPrice(item2Price);
        items.add(item1);
        items.add(item2);

        BigDecimal totalPrice = cartService.calculateTotalPrice(items);

        BigDecimal expectedTotalPrice = item1Price.add(item2Price).add(BigDecimal.valueOf(PACKAGING));
        assertEquals(expectedTotalPrice, totalPrice);
    }

    @Test
    void updateCartItemQuantity_ShouldUpdateQuantityForItem() {
        Long itemId = 1L;
        int newQuantity = 2;
        CartItem cartItem = new CartItem();
        cartItem.setId(itemId);
        cartItem.setMenuPrice(BigDecimal.TEN);
        cartItem.setQuantity(3);
        when(cartRepository.findByIdMenu(itemId)).thenReturn(cartItem);

        cartService.updateCartItemQuantity(itemId, newQuantity);
        assertEquals(newQuantity, cartItem.getQuantity());

        BigDecimal expectedTotalPrice = BigDecimal.TEN.multiply(BigDecimal.valueOf(newQuantity));
        assertEquals(expectedTotalPrice, cartItem.getTotalPrice());

        verify(cartRepository, times(1)).update(cartItem);
    }

    @Test
    void updateCartItemQuantity_WhenItemNotFound_ShouldReturnExceptionThrow() {
        Long itemId = 1L;
        int newQuantity = 1;
        when(cartRepository.findByIdMenu(itemId)).thenReturn(null);

        CartException exception = assertThrows(CartException.class, () -> cartService.updateCartItemQuantity(itemId, newQuantity));
        assertEquals("Item with id " + itemId + " not found", exception.getMessage());

        verify(cartRepository, never()).update(any());
    }

    @Test
    void canAddItemToCart_WhenItemsInCartIsNotPresent_ShouldReturnTrue() {
        Long userId = 1L;
        Long restaurantId = 1L;
        List<CartItem> cartItems = new ArrayList<>();

        boolean canAdd = cartService.canAddItemToCart(userId, restaurantId, cartItems);
        assertTrue(canAdd);
    }

    @Test
    void canAddItemToCart_WhenItemsInCartFromSameUserAndRestaurant_ShouldReturnTrue() {
        Long userId = 1L;
        Long restaurantId = 1L;
        List<CartItem> cartItems = new ArrayList<>();
        CartItem cartItem1 = new CartItem();
        cartItem1.setUserId(userId);
        cartItem1.setRestaurantId(restaurantId);
        cartItems.add(cartItem1);

        boolean canAdd = cartService.canAddItemToCart(userId, restaurantId, cartItems);
        assertTrue(canAdd);
    }

    @Test
    void canAddItemToCart_WhenItemsInCartFromDifferentRestaurant_ShouldReturnFalse() {
        Long userId = 1L;
        Long restaurantId = 1L;
        List<CartItem> cartItems = new ArrayList<>();
        CartItem cartItem1 = new CartItem();
        cartItem1.setUserId(userId);
        cartItem1.setRestaurantId(restaurantId);
        cartItems.add(cartItem1);

        Long anotherRestaurantId = 2L;
        CartItem cartItem2 = new CartItem();
        cartItem2.setUserId(userId);
        cartItem2.setRestaurantId(anotherRestaurantId);
        cartItems.add(cartItem2);

        boolean canAdd = cartService.canAddItemToCart(userId, restaurantId, cartItems);
        assertFalse(canAdd);
    }
}
