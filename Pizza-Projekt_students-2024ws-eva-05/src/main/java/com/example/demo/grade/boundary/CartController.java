package com.example.demo.grade.boundary;

import com.example.demo.grade.domain.*;
import com.example.demo.grade.domain.enums.PizzaSize;
import com.example.demo.grade.repository.OrderRepository;
import com.example.demo.grade.repository.PizzaRepository;
import com.example.demo.grade.service.CartService;
import com.example.demo.grade.service.DeliveryAddressService;
import com.example.demo.grade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Controller
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    Logger logger = LoggerFactory.getLogger(CartController.class);

    @GetMapping({"/warenkorb"})
    public String viewCart(@AuthenticationPrincipal UserDetails userDetails, HttpSession session, Model model) {
        return cartService.viewCart(userDetails, session, model);
    }

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam Long pizzaId, @RequestParam int quantity, @RequestParam PizzaSize size,
                            @AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
        return cartService.addToCartHandler(pizzaId, quantity, size, userDetails, session);
    }

    @PostMapping("/remove-from-cart")
    public String removeFromCart(@RequestParam(value = "itemId", required = false) Long itemId,
                                 @AuthenticationPrincipal UserDetails userDetails, HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        return cartService.removeFromCartHandler(itemId, userDetails, session, redirectAttributes);
    }

    @GetMapping("/kaufen")
    public String kaufenPage(@AuthenticationPrincipal UserDetails userDetails, HttpSession session, Model model) {
        return cartService.kaufenPage(userDetails, session, model);
    }

    @PostMapping("/neue-address")
    public String saveNewAddress(@RequestParam String street, @RequestParam String houseNumber,
                                 @RequestParam String postalCode, @RequestParam String town,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        return cartService.saveNewAddress(street, houseNumber, postalCode, town, userDetails, redirectAttributes);
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam Long selectedAddressDTOid,
                           @AuthenticationPrincipal UserDetails userDetails) {
        return cartService.checkout(selectedAddressDTOid, userDetails);
    }
}
