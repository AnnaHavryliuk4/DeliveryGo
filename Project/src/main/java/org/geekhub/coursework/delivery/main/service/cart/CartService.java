package org.geekhub.coursework.delivery.main.service.cart;

import org.geekhub.coursework.delivery.main.exceptions.CartException;
import org.geekhub.coursework.delivery.main.exceptions.RestaurantException;
import org.geekhub.coursework.delivery.main.repository.cart.CartRepositoryImpl;
import org.geekhub.coursework.delivery.main.model.CartItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {
    private final CartRepositoryImpl cartRepository;

    private static final Double PACKAGING = 4.0;

    public CartService(CartRepositoryImpl cartRepository) {
        this.cartRepository = cartRepository;
    }

    public List<CartItem> getAllItemsInCart() {
        try {
            return cartRepository.getAllItemsInCart();
        } catch (Exception e) {
            throw new CartException("Failed get all items from cart: " + e.getMessage());
        }
    }

    public void removeItemFromCart(Long itemId) {
        try {
            cartRepository.removeItemFromCart(itemId);
        } catch (Exception e) {
            throw new CartException("Failed remove item from cart: " + e.getMessage());
        }
    }

    public void clearCartByUserId(Long userId) {
        try {
            cartRepository.clearCart(userId);
        } catch (Exception e) {
            throw new CartException("Failed clear cart: " + e.getMessage());
        }
    }

    public void addItemToCart(CartItem cartItem) {
        try {
            cartRepository.addItemToCart(cartItem);
        } catch (Exception e) {
            throw new CartException("Failed to add item to cart: " + e.getMessage());
        }
    }

    public List<CartItem> getCartItemsByUserId(Long userId) {
        try {
            return cartRepository.getCartItemsByUserId(userId);
        } catch (Exception e) {
            throw new CartException("Cart for user with id = " + userId + " is not found");
        }
    }

    public BigDecimal calculateTotalPrice(List<CartItem> items) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item : items) {
            totalPrice = totalPrice.add(item.getTotalPrice());
        }

        return totalPrice.add(BigDecimal.valueOf(PACKAGING));
    }

    public void updateCartItemQuantity(Long itemId, int newQuantity) {
        CartItem cartItem = cartRepository.findByIdMenu(itemId);

        if (cartItem != null) {
            BigDecimal totalPrice = cartItem.getMenuPrice().multiply(BigDecimal.valueOf(newQuantity));
            cartItem.setTotalPrice(totalPrice);
            cartItem.setQuantity(newQuantity);
            cartRepository.update(cartItem);
        } else {
            throw new CartException("Item with id " + itemId + " not found");
        }
    }

    public boolean canAddItemToCart(Long userId, Long restaurantId, List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getUserId().equals(userId) && !cartItem.getRestaurantId().equals(restaurantId)) {
                return false;
            }
        }

        return true;
    }

}
