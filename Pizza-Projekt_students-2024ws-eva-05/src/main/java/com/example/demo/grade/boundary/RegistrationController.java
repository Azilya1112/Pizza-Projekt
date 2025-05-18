package com.example.demo.grade.boundary;

import com.example.demo.grade.domain.User;
import com.example.demo.grade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import com.example.demo.grade.domain.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpSession;
import com.example.demo.grade.service.CartService;


@Controller
public class RegistrationController {
  
  private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private CartService cartService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register"; 
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, @RequestParam String confirmPassword, 
                            HttpSession session) {

        if (!password.equals(confirmPassword)) {
            return "redirect:/register?error"; 
        }

        User user = new User(username, passwordEncoder.encode(password), Arrays.asList(Role.USER));
        userService.saveUser(user);
        
        Long userId = userService.getUserIdByUsername(user.getUsername());
        cartService.transferCartToUser(userId, session);

        return "redirect:/login"; 
    }

}