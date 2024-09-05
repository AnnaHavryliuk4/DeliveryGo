package org.geekhub.coursework.delivery.main.controller;

import jakarta.servlet.http.HttpSession;
import org.geekhub.coursework.delivery.authorization.service.UserService;
import org.geekhub.coursework.delivery.main.service.restaurant.MenuItemService;
import org.geekhub.coursework.delivery.main.service.restaurant.RestaurantsService;
import org.geekhub.coursework.delivery.main.model.CartItem;
import org.geekhub.coursework.delivery.main.model.MenuItem;
import org.geekhub.coursework.delivery.main.model.Restaurant;
import org.geekhub.coursework.delivery.main.service.cart.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class MenuItemsController {
    private final RestaurantsService restaurantService;
    private final MenuItemService menuItemService;
    private final CartService cartService;
    private final UserService userService;

    public MenuItemsController(MenuItemService menuItemService, RestaurantsService restaurantService, CartService cartService, UserService userService) {
        this.menuItemService = menuItemService;
        this.restaurantService = restaurantService;
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/restaurant/{restaurantId}/menu")
    public String showRestaurantMenu(@PathVariable Long restaurantId, Model model, HttpSession session) {
        Long userId = userService.getUserId();
        if (userId == null) {
            return "redirect:/login";
        }

        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);
        List<MenuItem> menuItems = menuItemService.findMenuItemsByRestaurantId(restaurant.getId());
        List<CartItem> cartItems = cartService.getAllItemsInCart();
        boolean canAddItem = cartService.canAddItemToCart(userId, restaurantId, cartItems);
        if (!canAddItem) {
            model.addAttribute("errorMessage", "You can only add items from the same restaurant to your cart.");
        }
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("menuItems", menuItems);
        session.setAttribute("userId", userId);
        return "restaurant_menu";
    }
}

