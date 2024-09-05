package org.geekhub.coursework.delivery.main.service.restaurant;

import org.geekhub.coursework.delivery.main.exceptions.RestaurantException;
import org.geekhub.coursework.delivery.main.model.Restaurant;
import org.geekhub.coursework.delivery.main.repository.restaurant.RestaurantsRepositoryImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantsServiceTest {

    @Mock
    private RestaurantsRepositoryImp restaurantsRepository;
    @Mock
    private FileServer fileServer;
    private RestaurantsService restaurantsService;

    @BeforeEach
    void setUp() {
        restaurantsService = new RestaurantsService(restaurantsRepository, fileServer);
    }

    @Test
    void saveRestaurant_whenFileUploadSuccessfully() throws IOException {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Test restaurant");

        byte[] imageBytes = "test image content".getBytes();
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", imageBytes);
        restaurantsService.saveRestaurant(restaurant, image);

        verify(restaurantsRepository, times(1)).save(restaurant);
        verify(fileServer, times(1)).saveFile(anyString(), eq(image), eq("Test restaurant.jpg"));

    }

    @Test
    void deleteRestaurant_whenDeleteSuccessfully() throws IOException {
        Long id = 1L;
        String imageName = "TestImage.jpg";
        Restaurant restaurant = new Restaurant();
        restaurant.setId(id);
        restaurant.setName("TestImage");
        when(restaurantsRepository.findById(id)).thenReturn(restaurant);

        restaurantsService.deleteRestaurant(id);

        verify(restaurantsRepository, times(1)).findById(id);
        verify(restaurantsRepository, times(1)).delete(id);
        verify(fileServer, times(1)).deleteFile(anyString(), eq(imageName));
    }

    @Test
    void deleteRestaurant_whenDeleteFailed_shouldReturnIOException() throws IOException {
        long id=1L;
        when(restaurantsRepository.findById(id)).thenThrow(new RestaurantException("Failed delete restaurant with id = " + id + ":File deletion filed"));
        doThrow(new RestaurantException("File deletion failed")).when(fileServer).deleteFile(anyString(), anyString());

        RestaurantException exception = assertThrows(RestaurantException.class,
            () -> restaurantsRepository.delete(id));
        assertEquals("Failed delete restaurant with id = " + id + ":File deletion filed", exception.getMessage());
    }

    @Test
    void findAllRestaurants_whenRepositoryReturnAllItems() {
        Restaurant restaurant1 = new Restaurant();
        Restaurant restaurant2 = new Restaurant();

        restaurant1.setName("Restaurant1");
        restaurant2.setName("Restaurant2");

        List<Restaurant> expectedRestaurants = List.of(restaurant1, restaurant2);
        when(restaurantsRepository.findAll()).thenReturn(expectedRestaurants);

        List<Restaurant> actualRestaurants = restaurantsService.findAllRestaurants();
        assertEquals(expectedRestaurants, actualRestaurants);
    }

    @Test
    void findAllRestaurants_whenFailedReturnItemsFromRepo_shouldReturnRestaurantException() {
        when(restaurantsRepository.findAll()).thenThrow(new RuntimeException("Database connection failed"));
        assertThrows(RestaurantException.class, () -> restaurantsService.findAllRestaurants());
    }

    @Test
    void findRestaurantById_whenRestaurantIdIsPresent() {
        Long restaurantId = 1L;
        Restaurant expectedRestaurant = new Restaurant();
        expectedRestaurant.setId(restaurantId);

        when(restaurantsRepository.findById(restaurantId)).thenReturn(expectedRestaurant);

        Restaurant actualRestaurant = restaurantsService.findRestaurantById(restaurantId);
        assertEquals(expectedRestaurant, actualRestaurant);
    }

    @Test
    void findRestaurantById_whenRestaurantNotFound_shouldReturnRestaurantException() {
        Long restaurantId = 1L;
        when(restaurantsRepository.findById(restaurantId)).thenThrow(new RestaurantException("Restaurant with id = " + restaurantId + " is not found"));

        RestaurantException exception = assertThrows(RestaurantException.class,
            () -> restaurantsService.findRestaurantById(restaurantId));
        assertEquals("Restaurant with id = " + restaurantId + " is not found", exception.getMessage());
    }

    @Test
    void findAllRestaurantsWithDelivery_getRestaurantWithDeliveryMethod() {
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setId(1L);
        restaurant1.setDeliveryMethod("delivery");
        Restaurant restaurant2 = new Restaurant();
        restaurant2.setId(2L);
        restaurant1.setDeliveryMethod("delivery");

        List<Restaurant> expectedRestaurants = List.of(restaurant1, restaurant2);
        when(restaurantsRepository.findAllRestaurantsWithDelivery()).thenReturn(expectedRestaurants);

        List<Restaurant> actualRestaurants = restaurantsRepository.findAllRestaurantsWithDelivery();
        assertEquals(expectedRestaurants, actualRestaurants);
    }

    @Test
    void findAllRestaurantsWithDelivery_whenFailedGetRestaurantWithDeliveryMethod() {
        when(restaurantsRepository.findAllRestaurantsWithDelivery()).thenThrow(new RestaurantException("restaurant is not found"));

        RestaurantException exception = assertThrows(RestaurantException.class,
            () -> restaurantsService.findAllRestaurantsWithDelivery());
        assertEquals("Failed to get all restaurants with delivery: restaurant is not found", exception.getMessage());
    }

    @Test
    void findAllRestaurantsWithDelivery_getRestaurantWithTakeoutMethod() {
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setId(1L);
        restaurant1.setDeliveryMethod("takeout");
        Restaurant restaurant2 = new Restaurant();
        restaurant2.setId(2L);
        restaurant1.setDeliveryMethod("takeout");

        List<Restaurant> expectedRestaurants = List.of(restaurant1, restaurant2);
        when(restaurantsRepository.findAllRestaurantsWithTakeout()).thenReturn(expectedRestaurants);

        List<Restaurant> actualRestaurants = restaurantsRepository.findAllRestaurantsWithTakeout();
        assertEquals(expectedRestaurants, actualRestaurants);
    }

    @Test
    void findAllRestaurantsWithDelivery_whenFailedGetRestaurantWithTakeoutMethod() {
        when(restaurantsRepository.findAllRestaurantsWithTakeout()).thenThrow(new RestaurantException("restaurant is not found"));

        RestaurantException exception = assertThrows(RestaurantException.class,
            () -> restaurantsService.findAllRestaurantsWithTakeout());
        assertEquals("Failed to get all restaurants with takeout: restaurant is not found", exception.getMessage());
    }


}
