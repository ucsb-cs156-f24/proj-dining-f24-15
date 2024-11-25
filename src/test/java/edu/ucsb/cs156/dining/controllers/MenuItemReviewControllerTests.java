package edu.ucsb.cs156.dining.controllers;

import edu.ucsb.cs156.dining.repositories.UserRepository;
import edu.ucsb.cs156.dining.services.CurrentUserService;
import edu.ucsb.cs156.dining.testconfig.TestConfig;
import edu.ucsb.cs156.dining.ControllerTestCase;
import edu.ucsb.cs156.dining.entities.MenuItem;
import edu.ucsb.cs156.dining.entities.MenuItemReview;
import edu.ucsb.cs156.dining.models.CurrentUser;
import edu.ucsb.cs156.dining.repositories.MenuItemRepository;
import edu.ucsb.cs156.dining.repositories.MenuItemReviewRepository;
import edu.ucsb.cs156.dining.entities.User;
import edu.ucsb.cs156.dining.models.CurrentUser;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = MenuItemReviewController.class)
@Import(TestConfig.class)

public class MenuItemReviewControllerTests extends ControllerTestCase {
    @MockBean
     MenuItemReviewRepository menuItemReviewRepository;

    @MockBean
    MenuItemRepository menuItemRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    CurrentUserService currentUserService;
    
    
    // GET BY ID
    
    @WithMockUser(roles = { "USER" })
    @Test
    public void user_can_get_their_reviews() throws Exception {
        // Mock a review
        MenuItemReview review1 = MenuItemReview.builder()
            .id(1L)
            .studentUserId(100L)
            .status("Awaiting Moderation")
            .build();

        when(menuItemReviewRepository.findByStudentUserId(100L))
            .thenReturn(Arrays.asList(review1));

        User mockUser = User.builder().id(100L).build();
        CurrentUser mockCurrentUser = CurrentUser.builder().user(mockUser).build();

        when(currentUserService.getCurrentUser()).thenReturn(mockCurrentUser);

        MvcResult result = mockMvc.perform(get("/api/menuitemreviews/reviews"))
            .andExpect(status().isOk())
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains("\"id\":1"));
        verify(menuItemReviewRepository, times(1)).findByStudentUserId(100L);
}

}
