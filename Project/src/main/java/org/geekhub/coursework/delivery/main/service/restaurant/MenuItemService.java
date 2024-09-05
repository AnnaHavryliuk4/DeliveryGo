package org.geekhub.coursework.delivery.main.service.restaurant;

import org.geekhub.coursework.delivery.main.exceptions.RestaurantException;
import org.geekhub.coursework.delivery.main.model.MenuItem;
import org.geekhub.coursework.delivery.main.repository.restaurant.MenuItemRepositoryImpl;
import org.geekhub.coursework.delivery.main.service.restaurant.FileUploadUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Service
public class MenuItemService {
    private final MenuItemRepositoryImpl menuItemRepository;

    private final FileServer fileServer;

    public MenuItemService(MenuItemRepositoryImpl menuItemRepository, FileServer fileServer) {
        this.menuItemRepository = menuItemRepository;
        this.fileServer = fileServer;
    }

    public List<MenuItem> findMenuItemsByRestaurantId(Long restaurantId) {
        try {
            List<MenuItem> menuItems = menuItemRepository.findByRestaurantId(restaurantId);
            menuItems.sort(Comparator.comparing(MenuItem::getName));
            return menuItems;
        } catch (Exception e) {
            throw new RestaurantException("Menu of restaurant with id = " + restaurantId + " is not found");
        }
    }

    public void saveDish(MenuItem menuItem, MultipartFile image) throws IOException {
        menuItemRepository.save(menuItem);

        String imageName = menuItem.getName() + ".jpg";
        String uploadFile = "Coursework/src/main/resources/static/images/restaurants";
        fileServer.saveFile(uploadFile,image,imageName);
    }

    public void deleteDish(long id) {
        try {
            MenuItem menuItem = menuItemRepository.findById(id);
            menuItemRepository.delete(id);
            String imageName = menuItem.getName()+".jpg";

            String uploadFile = "Coursework/src/main/resources/static/images/restaurants";
            fileServer.deleteFile(uploadFile, imageName);
        } catch (Exception e) {
            throw new RestaurantException("Failed delete dish with id = " + id + ":" + e.getMessage());
        }
    }

    public void updateDish(MenuItem menuItem) {
        try {
            menuItemRepository.update(menuItem);
        } catch (Exception e) {
            throw new RestaurantException("Failed update menu items: " + e.getMessage());
        }
    }

    public MenuItem findDishById(Long id) {
        try {
            return menuItemRepository.findById(id);
        } catch (Exception e) {
            throw new RestaurantException("Dish with id = " + id + " is not found");
        }
    }
}
