package org.geekhub.coursework.delivery.main.controller;

import org.geekhub.coursework.delivery.authorization.service.UserService;
import org.geekhub.coursework.delivery.main.service.restaurant.RestaurantsService;
import org.geekhub.coursework.delivery.main.model.Restaurant;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class RestaurantsController {
    private final RestaurantsService restaurantsService;
    private final UserService userService;

    public RestaurantsController(RestaurantsService restaurantsService, UserService userService) {
        this.restaurantsService = restaurantsService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String showMainPage(Model model) {
        Long userId = userService.getUserId();
        List<Restaurant> restaurants = restaurantsService.findAllRestaurants();
        model.addAttribute("restaurants", restaurants);
        model.addAttribute("delivery", "");
        model.addAttribute("userId", userId);
        return "mainPage";
    }

    @GetMapping("/delivery")
    public String delivery(Model model) {
        List<Restaurant> restaurants = restaurantsService.findAllRestaurantsWithDelivery();
        model.addAttribute("restaurants", restaurants);
        return "mainPage";
    }

    @GetMapping("/takeout")
    public String pickup(Model model) {
        List<Restaurant> restaurants = restaurantsService.findAllRestaurantsWithTakeout();
        model.addAttribute("restaurants", restaurants);
        return "mainPage";
    }
}
