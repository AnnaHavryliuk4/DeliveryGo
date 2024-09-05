package org.geekhub.coursework.delivery.main.service.restaurant;

import org.geekhub.coursework.delivery.main.exceptions.RestaurantException;
import org.geekhub.coursework.delivery.main.model.MenuItem;
import org.geekhub.coursework.delivery.main.model.Restaurant;
import org.geekhub.coursework.delivery.main.repository.restaurant.RestaurantsRepositoryImp;
import org.geekhub.coursework.delivery.main.service.restaurant.FileUploadUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class RestaurantsService {
    private final RestaurantsRepositoryImp restaurantRepository;
    private final FileServer fileServer;

    public RestaurantsService(RestaurantsRepositoryImp restaurantsRepository, FileServer fileServer) {
        this.restaurantRepository = restaurantsRepository;
        this.fileServer = fileServer;
    }

    public void saveRestaurant(Restaurant restaurant, MultipartFile image) throws IOException {
        restaurantRepository.save(restaurant);

        String imageName = restaurant.getName() + ".jpg";
        String uploadFile = "Coursework/src/main/resources/static/images/restaurants";
        fileServer.saveFile(uploadFile,image,imageName);
    }

    public void deleteRestaurant(long id) {
        try {
            Restaurant restaurant = restaurantRepository.findById(id);
            restaurantRepository.delete(id);
            String imageName = restaurant.getName()+".jpg";

            String uploadFile = "Coursework/src/main/resources/static/images/restaurants";
            fileServer.deleteFile(uploadFile, imageName);
        } catch (Exception e) {
            throw new RestaurantException("Failed delete restaurant with id = " + id + ": " + e.getMessage());
        }
    }

    public List<Restaurant> findAllRestaurants() {
        try {
            return restaurantRepository.findAll();
        } catch (Exception e) {
            throw new RestaurantException("Failed to get all restaurants: " + e.getMessage());
        }
    }

    public Restaurant findRestaurantById(Long id) {
        try {
            return restaurantRepository.findById(id);
        } catch (Exception e) {
            throw new RestaurantException("Restaurant with id = " + id + " is not found");
        }
    }

    public List<Restaurant> findAllRestaurantsWithDelivery() {
        try {
            return restaurantRepository.findAllRestaurantsWithDelivery();
        } catch (Exception e) {
            throw new RestaurantException("Failed to get all restaurants with delivery: " + e.getMessage());
        }
    }

    public List<Restaurant> findAllRestaurantsWithTakeout() {
        try {
            return restaurantRepository.findAllRestaurantsWithTakeout();
        } catch (Exception e) {
            throw new RestaurantException("Failed to get all restaurants with takeout: " + e.getMessage());
        }
    }
}
