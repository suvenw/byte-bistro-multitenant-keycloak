package com.langworthytech.bytebistro.controller;

import com.langworthytech.bytebistro.model.MenuItem;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuItemController {

    @GetMapping("/admin")
    public List<MenuItem> getAdminMenuItems() {
        return Arrays.asList(
                new MenuItem(1L, "Admin Denver Omelet", "Add white or wheat toast.", 12.99),
                new MenuItem(2L, "Admin Side of White Gravy", "Cooked in Grandma's kitchen.", 2.99),
                new MenuItem(3L, "Admin Biscuits and Gravy", "Baked fresh daily!", 5.99),
                new MenuItem(4L, "Admin Grand Slam breakfast", "For the hungry guys.", 20.99)
        );
    }

    @GetMapping
    @PostAuthorize("returnObject.username = authentication.token.subject")
    public List<MenuItem> getMenuItems(@AuthenticationPrincipal Jwt token) {

        System.out.println("Subject: " + token.getSubject());
        System.out.println("Claims: " + token.getClaims().toString());
        System.out.println("Issuer: " + token.getIssuer());
        System.out.println("Id: " + token.getId());

        return Arrays.asList(
                new MenuItem(1L, "Denver Omelet", "Add white or wheat toast.", 12.99),
                new MenuItem(2L, "Side of White Gravy", "Cooked in Grandma's kitchen.", 2.99),
                new MenuItem(3L, "Biscuits and Gravy", "Baked fresh daily!", 5.99),
                new MenuItem(4L, "Grand Slam breakfast", "For the hungry guys.", 20.99)
        );
    }

//    @PostMapping("/admin")
//    public ResponseEntity<HttpStatus> createMenuItem(@RequestMapping MenuItem menuItem) {
//
//    }
}
