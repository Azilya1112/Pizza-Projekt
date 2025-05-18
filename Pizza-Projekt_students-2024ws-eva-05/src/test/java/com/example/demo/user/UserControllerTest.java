package com.example.demo.user;


import com.example.demo.grade.boundary.UserController;
import com.example.demo.grade.domain.User;
import com.example.demo.grade.service.DeliveryAddressService;
import com.example.demo.grade.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.view.InternalResourceView;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private DeliveryAddressService deliveryAddressService;

    @Mock
    private Principal principal;

    @Mock
    private Model model;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setViewResolvers((viewName, locale) -> new InternalResourceView("/WEB-INF/views/" + viewName + ".html"))
                .build();

    }

    @Test
    void getProfilePage_shouldReturnProfileSettingsPage() throws Exception {

        User user = new User();
        user.setUsername("testUser");

        when(principal.getName()).thenReturn("testUser");
        when(userService.getUserByUsername("testUser")).thenReturn(Optional.of(user));
        when(deliveryAddressService.getAllAddresses(user)).thenReturn(List.of());


        mockMvc.perform(get("/profile-settings").principal(principal))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-settings"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("addresses"));

        verify(userService, times(1)).getUserByUsername("testUser");
        verify(deliveryAddressService, times(1)).getAllAddresses(user);
    }

    @Test
    void getProfilePage_whenUserNotFound_shouldThrowException() {

        when(principal.getName()).thenReturn("nonexistentUser");
        when(userService.getUserByUsername("nonexistentUser")).thenReturn(Optional.empty());


        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            userController.getProfilePage(model, principal);
        });

        assertEquals("User not found", thrown.getMessage());

        verify(userService, times(1)).getUserByUsername("nonexistentUser");
    }

    @Test
    void updateUser_shouldReturnProfileSettingsOnError() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");


        doThrow(new RuntimeException("Update failed")).when(userService).updateUserProfile(1L, "testUser", "oldPassword", "newPassword");


        mockMvc.perform(post("/profile-settings/update-profile")
                        .flashAttr("user", user)
                        .param("currentPassword", "oldPassword")
                        .param("newPassword", "newPassword"))
                .andExpect(status().isOk()) // <-- Ожидаем статус 200
                .andExpect(view().name("profile-settings")) // <-- Представление profile-settings
                .andExpect(model().attributeExists("errorMessage")); // <-- Убедитесь, что сообщение ошибки присутствует

        verify(userService, times(1)).updateUserProfile(1L, "testUser", "oldPassword", "newPassword");
    }


    @Test
    void updateUser_shouldReturnErrorMessageOnFailure() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        doThrow(new RuntimeException("Update failed")).when(userService).updateUserProfile(1L, "testUser", "oldPassword", "newPassword");

        mockMvc.perform(post("/profile-settings/update-profile")
                        .flashAttr("user", user)
                        .param("currentPassword", "oldPassword")
                        .param("newPassword", "newPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-settings"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(userService, times(1)).updateUserProfile(1L, "testUser", "oldPassword", "newPassword");
    }


}
