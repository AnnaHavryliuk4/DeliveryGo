package org.geekhub.coursework.delivery.main.controller;

import org.geekhub.coursework.delivery.authorization.service.UserService;
import org.geekhub.coursework.delivery.main.model.Order;
import org.geekhub.coursework.delivery.main.service.restaurant.MenuItemService;
import org.geekhub.coursework.delivery.main.service.order.OrderService;
import org.geekhub.coursework.delivery.main.service.restaurant.RestaurantsService;
import org.geekhub.coursework.delivery.main.model.MenuItem;
import org.geekhub.coursework.delivery.main.model.Restaurant;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class AdminController {
    private final RestaurantsService service;
    private final MenuItemService menuItemService;
    private final UserService userService;

    private final OrderService orderService;

    public AdminController(RestaurantsService service, MenuItemService menuItemService, UserService userService, OrderService orderService) {
        this.service = service;
        this.menuItemService = menuItemService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin_panel";
    }

    @GetMapping("/admin/manage_restaurants")
    public String adminPageRestaurants(Model model) {
        List<Restaurant> restaurants = service.findAllRestaurants();
        model.addAttribute("restaurants", restaurants);

        return "manage_restaurants";
    }

    @PostMapping("/delete/{id}")
    public String deleteRestaurant(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.deleteRestaurant(id);
        redirectAttributes.addFlashAttribute("successMessage", "Changes successfully added");

        return "redirect:/admin/manage_restaurants";
    }

    @PostMapping("/admin/add")
    public String addRestaurant(@RequestParam String name,
                                @RequestParam String deliveryMethod,
                                @RequestParam MultipartFile image,
                                RedirectAttributes redirectAttributes) throws IOException {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setDeliveryMethod(deliveryMethod);
        service.saveRestaurant(restaurant, image);
        redirectAttributes.addFlashAttribute("successMessage", "Changes successfully added");

        return "redirect:/admin/manage_restaurants";
    }

    @GetMapping("/{restaurantId}/menu")
    public String showRestaurantMenuAdmin(@PathVariable Long restaurantId,
                                          Model model) {
        Restaurant restaurant = service.findRestaurantById(restaurantId);
        List<MenuItem> menuItems = menuItemService.findMenuItemsByRestaurantId(restaurant.getId());
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("menuItems", menuItems);

        return "menage_rastaurant_menu";
    }

    @PostMapping("/{restaurantId}/add")
    public String addMenuItem(@PathVariable Long restaurantId,
                              @RequestParam String name,
                              @RequestParam BigDecimal price,
                              @RequestParam Integer weight,
                              @RequestParam Integer serving,
                              @RequestParam String description,
                              @RequestParam MultipartFile image,
                              RedirectAttributes redirectAttributes) throws IOException {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(name);
        menuItem.setPrice(price);
        menuItem.setWeight(weight);
        menuItem.setServing(serving);
        menuItem.setDescription(description);
        menuItem.setRestaurantId(restaurantId);
        menuItemService.saveDish(menuItem, image);
        redirectAttributes.addFlashAttribute("successMessage", "Changes successfully added");

        return "redirect:/{restaurantId}/menu";
    }

    @PostMapping("/{restaurantId}/delete/{id}")
    public String deleteMenuItem(@PathVariable Long restaurantId,
                                 @PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        menuItemService.deleteDish(id);
        redirectAttributes.addFlashAttribute("successMessage", "Changes successfully added");

        return "redirect:/{restaurantId}/menu";
    }

    @GetMapping("{restaurantId}/edit/{id}")
    public String showEditMenuItem(@PathVariable Long restaurantId,
                                   @PathVariable Long id,
                                   Model model) {
        model.addAttribute("restaurantId", restaurantId);
        model.addAttribute("menuItem", menuItemService.findDishById(id));

        return "edit_menu";
    }

    @PostMapping("/{restaurantId}/update/{id}")
    public String updateMenuItem(@PathVariable Long restaurantId,
                                 @PathVariable Long id,
                                 @RequestParam String name,
                                 @RequestParam BigDecimal price,
                                 @RequestParam Integer weight,
                                 @RequestParam Integer serving,
                                 @RequestParam String description,
                                 RedirectAttributes redirectAttributes) {
        MenuItem menuItem = new MenuItem();
        menuItem.setId(id);
        menuItem.setName(name);
        menuItem.setPrice(price);
        menuItem.setWeight(weight);
        menuItem.setServing(serving);
        menuItem.setDescription(description);
        menuItemService.updateDish(menuItem);
        redirectAttributes.addFlashAttribute("successMessage", "Changes successfully added");

        return "redirect:/{restaurantId}/menu";
    }

    @GetMapping("/admin/manage_customers")
    public String getAllUsersWithRoleUser(Model model) {
        model.addAttribute("users", userService.getAllUsersWithRoleUser());

        return "manage_customers";
    }

    @GetMapping("/admin/get-user-orders/{userId}")
    public String getUserOrders(@PathVariable Long userId, Model model) {
        List<Order> orders = orderService.getOrdersForUser(userId);
        List<Order> orderItems = orderService.getOrdersItemForUser(orders);
        model.addAttribute("orders", orders);
        model.addAttribute("cart", orderItems);

        return "admin_user_orders";
    }


}
