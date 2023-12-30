package com.langworthytech.bytebistro.controller;

import com.langworthytech.bytebistro.repository.MenuItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URL;
import java.time.Instant;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ContextConfiguration
//@SpringBootTest
@ExtendWith(SpringExtension.class)
class MenuItemControllerTest {

    @Mock
    private MenuItemRepository mockMenuItemRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setup(WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @WithMockUser(roles = "create-menu")
    void getAdminMenuItems() throws Exception {

        MenuItemController menuItemController = new MenuItemController(mockMenuItemRepository);

        Jwt mockJwt = mock(Jwt.class);
        doReturn("dummy subject").when(mockJwt).getSubject();
        doReturn(new HashMap<>()).when(mockJwt).getClaims();
        doReturn(new URL("http://localhost")).when(mockJwt).getIssuer();
        doReturn("dummy id").when(mockJwt).getId();



        this.mockMvc.perform(MockMvcRequestBuilders.get("/menu")).andExpect(status().isOk());
    }

    @Test
    void getMenuItems() {
    }

    @Test
    void createMenuItem() {
    }
}