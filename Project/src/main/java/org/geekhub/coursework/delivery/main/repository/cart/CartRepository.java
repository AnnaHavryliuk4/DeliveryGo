package org.geekhub.coursework.delivery.main.repository.cart;

import org.geekhub.coursework.delivery.main.model.CartItem;

import java.util.List;

public interface CartRepository {
    List<CartItem> getAllItemsInCart();

    CartItem findByIdMenu(Long menuId);

    void clearCart(Long userId);

    void removeItemFromCart(Long menuId);

    void addItemToCart(CartItem cartItem);

    void update(CartItem cartItem);

}
