package org.geekhub.coursework.delivery.main.service.restaurant;

import org.geekhub.coursework.delivery.main.exceptions.CartException;
import org.geekhub.coursework.delivery.main.exceptions.RestaurantException;
import org.geekhub.coursework.delivery.main.model.MenuItem;
import org.geekhub.coursework.delivery.main.repository.restaurant.MenuItemRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceTest {
    @Mock
    private MenuItemRepositoryImpl menuItemRepository;
    @Mock
    private FileServer fileServer;
    private MenuItemService menuItemService;

    @BeforeEach
    void setUp() {
        menuItemService = new MenuItemService(menuItemRepository, fileServer);
    }

    @Test
    void findMenuItemsByRestaurantId_WhenMenuItemsIsPresent_shouldReturnMenuItems() {
        Long restaurantId = 1L;
        MenuItem menuItem1 = new MenuItem();
        MenuItem menuItem2 = new MenuItem();
        menuItem1.setRestaurantId(restaurantId);
        menuItem2.setRestaurantId(restaurantId);

        List<MenuItem> expectedMenuItems = List.of(menuItem1, menuItem2);
        when(menuItemRepository.findByRestaurantId(restaurantId)).thenReturn(expectedMenuItems);

        List<MenuItem> actualMenuItems = menuItemService.findMenuItemsByRestaurantId(restaurantId);
        assertEquals(expectedMenuItems, actualMenuItems);
    }

    @Test
    void findMenuItemsByRestaurantId_whenNotFound_shouldReturnRestaurantException() {
        Long restaurantId = 1L;
        when(menuItemRepository.findByRestaurantId(restaurantId)).thenReturn(Collections.emptyList());

        RestaurantException exception = assertThrows(RestaurantException.class,
            () -> menuItemService.findMenuItemsByRestaurantId(restaurantId));
        assertEquals("Menu of restaurant with id = " + restaurantId + " is not found", exception.getMessage());
    }

    @Test
    void saveDish_whenSaveSuccessfully() throws IOException {
        MenuItem menuItem = new MenuItem();
        menuItem.setName("Test Dish");

        byte[] imageBytes = "test image content".getBytes();
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", imageBytes);
        menuItemService.saveDish(menuItem, image);

        verify(menuItemRepository, times(1)).save(menuItem);
        verify(fileServer, times(1)).saveFile(anyString(), eq(image), eq("Test Dish.jpg"));

    }

    @Test
    void deleteDish_whenDeleteSuccessfully() throws IOException {
        Long itemsId = 1L;
        String imageName = "TestImage.jpg";
        MenuItem menuItem = new MenuItem();
        menuItem.setId(itemsId);
        menuItem.setName("TestImage");
        when(menuItemRepository.findById(itemsId)).thenReturn(menuItem);

        menuItemService.deleteDish(itemsId);

        verify(menuItemRepository, times(1)).findById(itemsId);
        verify(menuItemRepository, times(1)).delete(itemsId);
        verify(fileServer, times(1)).deleteFile(anyString(), eq(imageName));

    }

    @Test
    void deleteDish_whenDeleteFailed_shouldReturnIOException() throws IOException {
        Long itemId = 1L;
        MenuItem menuItem = new MenuItem();
        menuItem.setId(itemId);
        menuItem.setName("TestImage");
        when(menuItemRepository.findById(itemId)).thenReturn(menuItem);
        doThrow(new RestaurantException("File deletion filed")).when(fileServer).deleteFile(anyString(), anyString());

        RestaurantException exception = assertThrows(RestaurantException.class,
            () -> menuItemService.deleteDish(itemId));
        assertEquals("Failed delete dish with id = " + itemId + ":File deletion filed", exception.getMessage());
    }

    @Test
    void updateDish_whenUpdateSuccessfully() {
        Long itemId = 1L;
        MenuItem menuItem = new MenuItem();
        menuItem.setId(itemId);
        menuItem.setName("Test update");

        menuItemService.updateDish(menuItem);
        verify(menuItemRepository, times(1)).update(menuItem);
    }

    @Test
    void updateDish_whenUpdateFailed_shouldReturnRestaurantException() {
        Long itemId = 1L;
        MenuItem menuItem = new MenuItem();
        menuItem.setId(itemId);
        menuItem.setName("Test update");
        doThrow(new RuntimeException("Update failed")).when(menuItemRepository).update(menuItem);

        RestaurantException exception = assertThrows(RestaurantException.class,
            () -> menuItemService.updateDish(menuItem));
        assertEquals("Failed update menu items: Update failed", exception.getMessage());
    }

    @Test
    void findDishById_whenDishIsPresent() {
        Long itemId = 1L;
        MenuItem menuItem = new MenuItem();
        menuItem.setId(itemId);

        menuItemService.findDishById(itemId);
        verify(menuItemRepository, times(1)).findById(itemId);
    }

    @Test
    void findDishById_whenDishIsNotFound() {
        Long itemId = 1L;
        when(menuItemRepository.findById(itemId)).thenReturn(null);

        RestaurantException exception = assertThrows(RestaurantException.class,
            () -> menuItemService.findDishById(itemId));
        assertEquals("Dish with id = " + itemId + " is not found", exception.getMessage());

        verify(menuItemRepository, never()).update(any());
    }
}
