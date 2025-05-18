package com.example.demo.security;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "user", roles = {"USER"})
    void userShouldNotAccessAdminPage() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden()); 
    }


    @Test
    void unauthenticatedUserShouldBeRedirectedToLoginPage() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection()) 
                .andExpect(result -> result.getResponse().getRedirectedUrl().contains("/login"));
    }

    @Test
    void anyoneShouldAccessHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }



    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminShouldAccessCartPage() throws Exception {
        mockMvc.perform(get("/warenkorb"))
                .andExpect(status().isOk()); 
    }


    @Test
    void unauthenticatedUserShouldAccessCartPage() throws Exception {
        mockMvc.perform(get("/warenkorb"))
                .andExpect(status().isOk()); 
    }


    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "user", roles = {"USER"})
    void userShouldNotAccessAdminUsersPage() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden()); 
    }



    @Test
    void unauthenticatedUserShouldBeRedirectedToLoginForUsersPage() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().is3xxRedirection()) 
                .andExpect(result -> result.getResponse().getRedirectedUrl().contains("/login"));
    }

}
