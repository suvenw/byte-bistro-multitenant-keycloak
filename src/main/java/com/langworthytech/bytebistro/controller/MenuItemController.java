package com.langworthytech.bytebistro.controller;

import com.langworthytech.bytebistro.model.MenuItem;
import com.langworthytech.bytebistro.repository.MenuItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuItemController {

    private final MenuItemRepository menuItemRepository;

    public MenuItemController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_kitchen.admin') || hasRole('read-menu')")
    public List<MenuItem> getMenuItems() {
        return menuItemRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_kitchen.admin') || hasRole('create-menu')")
    public ResponseEntity<HttpStatus> createMenuItem(@AuthenticationPrincipal Jwt token, @RequestBody MenuItem menuItem) {

        menuItemRepository.save(menuItem);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
