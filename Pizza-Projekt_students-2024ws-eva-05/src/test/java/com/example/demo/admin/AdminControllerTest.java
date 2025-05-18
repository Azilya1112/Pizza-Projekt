package com.example.demo.admin;
import com.example.demo.grade.boundary.AdminController;
import com.example.demo.grade.domain.Cart;
import com.example.demo.grade.domain.Pizza;
import com.example.demo.grade.repository.UserRepository;
import com.example.demo.grade.service.CartService;
import com.example.demo.grade.service.FilesStorageService;
import com.example.demo.grade.service.PizzaService;
import com.example.demo.grade.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PizzaService pizzaService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private FilesStorageService filesStorageService;

    @MockBean
    private CartService cartService;

    @BeforeEach
    void setUp() {
        Pizza testPizza = new Pizza();
        testPizza.setId(1L);
        testPizza.setName("Test Pizza");
        testPizza.setBasePrice(10.99);
        testPizza.setImage("/images/test_pizza.png");

      
        Mockito.when(pizzaService.getAllPizzas()).thenReturn(List.of(testPizza));

   
        Mockito.when(pizzaService.getPizzaById(1L)).thenReturn(Optional.of(testPizza));

    
        Cart testCart = new Cart();
        testCart.setItems(List.of()); 
        Mockito.when(cartService.getCartForUser(Mockito.anyLong())).thenReturn(testCart);
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminShouldAccessPizzaList() throws Exception {
        mockMvc.perform(get("/admin/pizzas"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-main"))
                .andExpect(model().attributeExists("pizzas"))
                .andExpect(model().attribute("pizzas", instanceOf(List.class)));
    }
    


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminShouldAddNewPizzaWithMultipart() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test_image.png",
                "image/png",
                "Test Image Content".getBytes()
        );

        mockMvc.perform(multipart("/admin/pizzas") //
                        .file(imageFile)
                        .with(csrf())
                        .param("name", "Test Pizza")
                        .param("basePrice", "12.99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/pizzas"));

        Mockito.verify(pizzaService).savePizza(Mockito.any(Pizza.class));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminShouldAccessEditPizzaPage() throws Exception {
        mockMvc.perform(get("/admin/pizzas/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-pizza-edit"))
                .andExpect(model().attributeExists("pizza"))
                .andExpect(model().attribute("pizza", instanceOf(Pizza.class)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminShouldUpdatePizza() throws Exception {
        mockMvc.perform(post("/admin/pizzas/update/1")
                        .with(csrf())
                        .param("name", "Updated Pizza Name")
                        .param("basePrice", "15.99")
                        .param("image", "updated_image.png"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/pizzas"));

        Mockito.verify(pizzaService).updatePizza(Mockito.eq(1L), Mockito.eq("Updated Pizza Name"), Mockito.eq(15.99), Mockito.any());
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminShouldAccessCart() throws Exception {
        mockMvc.perform(get("/admin/warenkorb"))
                .andExpect(status().isOk())
                .andExpect(view().name("warenkorb"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminShouldAccessUserList() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("list-users"))
                .andExpect(model().attributeExists("users"));
    }


}
