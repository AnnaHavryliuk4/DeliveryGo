package org.geekhub.coursework.delivery.main.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.geekhub.coursework.delivery.authorization.service.UserService;
import org.geekhub.coursework.delivery.main.service.cart.CartService;
import org.geekhub.coursework.delivery.main.model.CartItem;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class CartController {
    private final CartService cartService;
    private final UserService userService;
    private static final Double PACKAGING = 4.0;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/cart")
    public String showAllItemsInCart(Model model) {
        Long userId = userService.getUserId();
        List<CartItem> сartItems = cartService.getCartItemsByUserId(userId);
        double packagingFee = PACKAGING;
        BigDecimal totalPrice = cartService.calculateTotalPrice(сartItems);

        model.addAttribute("cartItems", сartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("packagingFee", packagingFee);
        return "cart_page";
    }

    @PostMapping("/add-order")
    public String addOrder(Model model) {
        List<CartItem> cartItems = cartService.getCartItemsByUserId(userService.getUserId());
        if (cartItems.isEmpty()) {
            return "redirect:/cart?errorMessage=You cannot complete the order";
        } else {
            return "redirect:/order";
        }
    }

    @PostMapping("/cart/remove/{id}")
    public String removeItemFromCart(@PathVariable Long id,
                                     HttpServletRequest request) {

        cartService.removeItemFromCart(id);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PostMapping("/cart/add/{id}")
    public String addItemToCart(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam BigDecimal price,
                                @RequestParam int quantity,
                                @RequestParam Long restaurantId,
                                @RequestParam String restaurantName,
                                @RequestParam String deliveryMethod,
                                HttpServletRequest request,
                                Model model) {
        Long userId = userService.getUserId();

        List<CartItem> cartItems = cartService.getAllItemsInCart();
        boolean canAddItem = cartService.canAddItemToCart(userId, restaurantId, cartItems);
        if (!canAddItem) {
            return "redirect:/restaurant/" + restaurantId + "/menu";
        }
        CartItem cartItem = new CartItem();
        cartItem.setMenuId(id);
        cartItem.setMenuName(name);
        cartItem.setMenuPrice(price);
        cartItem.setQuantity(quantity);
        cartItem.setRestaurantId(restaurantId);
        cartItem.setUserId(userId);
        cartItem.setRestaurantName(restaurantName);
        cartItem.setRestaurantDeliveryMethod(deliveryMethod);
        cartService.addItemToCart(cartItem);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PostMapping("cart/update-quantity")
    public String updateCartItemQuantity(@RequestParam Long itemId,
                                         @RequestParam int newQuantity,
                                         HttpServletRequest request,
                                         Model model) {
        cartService.updateCartItemQuantity(itemId, newQuantity);
        List<CartItem> updatedCartItems = cartService.getAllItemsInCart();

        BigDecimal totalPrice = cartService.calculateTotalPrice(updatedCartItems);
        model.addAttribute("totalPrice", totalPrice);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

}
