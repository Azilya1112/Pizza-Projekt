package de.evaspringbuch.eva11mobpay;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@SpringBootTest 
@WithMockUser(username="user", password = "plainTextPassword")
public class HttpRequestTest {

    @Autowired private WebApplicationContext wac;
    private MockMvc mockMvc;
    
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
        		.webAppContextSetup(wac)
                .build();
    }
    
    
    @Test
    public void testChatUserAccount() throws Exception {
    	mockMvc.perform(
			 	get("/users/elisa")
			 	 .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
//        .andExpect(jsonPath("$.id", is(1)))
//        .andExpect(jsonPath("$.name", is("elisa")))
//        .andExpect(jsonPath("$.state", is("available")))
//        .andExpect(jsonPath("$.account.id", is(1)))
//        .andExpect(jsonPath("$.account.balance", is(100)));
    	.andExpect(content().json("{\"id\":1,\"name\":\"elisa\",\"state\":\"available\",\"account\":{\"id\":1,\"balance\":100}}"));
    	;
    }

    @Test
    public void testAccountDoesNotExist() throws Exception {
    	mockMvc.perform(
			 	get("/users/el")
			 	 .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
//        .andExpect(jsonPath("$.id", is(1)))
//        .andExpect(jsonPath("$.name", is("elisa")))
//        .andExpect(jsonPath("$.state", is("available")))
//        .andExpect(jsonPath("$.account.id", is(1)))
//        .andExpect(jsonPath("$.account.balance", is(100)));
//    	.andExpect(content().json("{\"id\":1,\"name\":\"elisa\",\"state\":\"available\",\"account\":{\"id\":1,\"balance\":100}}"));
    	;
    }
    
    @Test
    public void testTransfer() throws Exception {
    	mockMvc.perform(
			 	post("/users/elisa/payment")
			 	 .contentType(MediaType.APPLICATION_JSON)
    	.content("{\"to\":\"marga\",\"amount\":100}"))
        .andExpect(status().isCreated()) //;
    	.andExpect(content().json("{\"code\":\"Transfer ist erfolgreich durchgefuehrt\"}"));
    	;
    }
    
    

    
}