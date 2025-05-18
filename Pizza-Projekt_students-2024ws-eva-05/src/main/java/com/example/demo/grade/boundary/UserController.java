package com.example.demo.grade.boundary;

import com.example.demo.grade.domain.User;
import com.example.demo.grade.repository.UserRepository;
import com.example.demo.grade.service.DeliveryAddressService;
import com.example.demo.grade.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/profile-settings")
public class UserController {

    private final UserService userService;
    private final DeliveryAddressService deliveryAddressService;
    private UserRepository userRepository;

    public UserController(UserService userService, DeliveryAddressService deliveryAddressService) {
        this.userService = userService;
        this.deliveryAddressService = deliveryAddressService;
    }

    @GetMapping("")
    public String getProfilePage(Model model, Principal principal) {
        User user = userService.getUserByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        model.addAttribute("user", user);
        model.addAttribute("addresses", deliveryAddressService.getAllAddresses(user));
        return "profile-settings";
    }

    @PostMapping("/update-profile")
    public String updateUser(@ModelAttribute User user,
                             @RequestParam(value = "currentPassword", required = false) String currentPassword,
                             @RequestParam(value = "newPassword", required = false) String newPassword,
                             Model model) {
        try {
            userService.updateUserProfile(user.getId(), user.getUsername(), currentPassword, newPassword);
            return "redirect:/profile-settings";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "profile-settings";
        }
    }

    @PostMapping("/add-address")
    public String addAddress(@RequestParam("postalCode") String postalCode,
                             @RequestParam("town") String town,
                             @RequestParam("houseNumber") String houseNumber,
                             @RequestParam("street") String street,
                             Principal principal) {
        User user = userService.getUserByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        deliveryAddressService.addDeliveryAddress(user.getId(), postalCode, town, houseNumber, street);

        return "redirect:/profile-settings";
    }



    @PostMapping("/update-address")
    public String updateAddress(@RequestParam("addressId") Long addressId,
                                @RequestParam("street") String street,
                                @RequestParam("houseNumber") String houseNumber,
                                @RequestParam("postalCode") String postalCode,
                                @RequestParam("town") String town,
                                Principal principal) {

        User user = userService.getUserByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден."));

        deliveryAddressService.updateDeliveryAddress(addressId, user.getId(), street, houseNumber, postalCode, town);

        return "redirect:/profile-settings";
    }

    @PostMapping("/delete-address")
    public String deleteAddress(@RequestParam("addressId") Long addressId, Principal principal) {
        User user = userService.getUserByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден."));

        deliveryAddressService.removeUserFromAddress(addressId, user.getId());

        return "redirect:/profile-settings";
    }


}

