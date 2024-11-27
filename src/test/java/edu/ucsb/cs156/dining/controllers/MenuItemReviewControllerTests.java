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
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.ArgumentCaptor;

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

        // POST
        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/menuitemreviews/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_post_valid_menu_item_review() throws Exception {
                MenuItem mockMenuItem = new MenuItem();
                when(menuItemRepository.findById(1L)).thenReturn(Optional.of(mockMenuItem));

                User mockUser = User.builder().id(100L).build();
                CurrentUser mockCurrentUser = CurrentUser.builder().user(mockUser).build();

                when(currentUserService.getCurrentUser()).thenReturn(mockCurrentUser);

                LocalDateTime now = LocalDateTime.now();
                MenuItemReview mockReview = MenuItemReview.builder()
                        .id(1L)
                        .studentUserId(100L)
                        .menuItem(mockMenuItem)
                        .itemServedDate(LocalDateTime.parse("2024-11-24T09:00:00"))
                        .status("Awaiting Moderation")
                        .rating(5)
                        .reviewText("Great food!")
                        .createdDate(now)
                        .lastEditedDate(now)
                        .build();

                when(menuItemReviewRepository.save(any(MenuItemReview.class))).thenReturn(mockReview);

                // Perform POST request
                MvcResult result = mockMvc.perform(post("/api/menuitemreviews/post")
                        .with(csrf())
                        .param("itemId", "1")
                        .param("itemServedDate", "2024-11-24T09:00:00")
                        .param("rating", "5")
                        .param("reviewText", "Great food!"))
                        .andExpect(status().isOk())
                        .andReturn();

                String responseContent = result.getResponse().getContentAsString();
                assertTrue(responseContent.contains("\"id\":1"));
                assertTrue(responseContent.contains("\"studentUserId\":100"));
                assertTrue(responseContent.contains("\"status\":\"Awaiting Moderation\""));
                assertTrue(responseContent.contains("\"reviewText\":\"Great food!\""));
                assertTrue(responseContent.contains("\"createdDate\""));
                assertTrue(responseContent.contains("\"lastEditedDate\""));

                verify(menuItemRepository, times(1)).findById(1L);
                verify(menuItemReviewRepository, times(1)).save(any(MenuItemReview.class));
        }
               



        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_verify_setters() throws Exception {
                MenuItem mockMenuItem = new MenuItem();
                when(menuItemRepository.findById(1L)).thenReturn(Optional.of(mockMenuItem));

                User mockUser = User.builder()
                        .id(100L)
                        .build();

                CurrentUser mockCurrentUser = CurrentUser.builder()
                        .user(mockUser)
                        .build();

                when(currentUserService.getCurrentUser()).thenReturn(mockCurrentUser);

                when(menuItemReviewRepository.save(any(MenuItemReview.class))).thenAnswer(invocation -> {
                        MenuItemReview savedReview = invocation.getArgument(0);
                        assertEquals(100L, savedReview.getStudentUserId());
                        assertEquals(LocalDateTime.parse("2024-11-24T09:00:00"), savedReview.getItemServedDate());
                        assertEquals(mockMenuItem, savedReview.getMenuItem());
                        assertEquals("Awaiting Moderation", savedReview.getStatus());
                        assertEquals(5, savedReview.getRating());
                        assertEquals("Great food!", savedReview.getReviewText());
                        assertNotNull(savedReview.getCreatedDate());
                        assertNotNull(savedReview.getLastEditedDate());
                        return savedReview;
                });

                mockMvc.perform(post("/api/menuitemreviews/post")
                        .with(csrf())
                        .param("itemId", "1")
                        .param("itemServedDate", "2024-11-24T09:00:00")
                        .param("rating", "5")
                        .param("reviewText", "Great food!"))
                        .andExpect(status().isOk());
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_cannot_post_if_menu_item_does_not_exist() throws Exception {
                when(menuItemRepository.findById(1L)).thenReturn(Optional.empty());

                User mockUser = User.builder().id(100L).build();
                CurrentUser mockCurrentUser = CurrentUser.builder().user(mockUser).build();

                when(currentUserService.getCurrentUser()).thenReturn(mockCurrentUser);

                mockMvc.perform(post("/api/menuitemreviews/post")
                        .with(csrf())
                        .param("itemId", "1")
                        .param("itemServedDate", "2024-11-24T09:00:00")
                        .param("rating", "5")
                        .param("reviewText", "Great food!"))
                        .andExpect(status().isNotFound());

                verify(menuItemRepository, times(1)).findById(1L);
                verify(menuItemReviewRepository, times(0)).save(any(MenuItemReview.class));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_review() throws Exception {
                // arrange
                LocalDateTime now = LocalDateTime.now();
                MenuItem mockMenuItem = new MenuItem();
                when(menuItemRepository.findById(1L)).thenReturn(Optional.of(mockMenuItem));

                User mockUser = User.builder().id(100L).build();
                CurrentUser mockCurrentUser = CurrentUser.builder().user(mockUser).build();

                when(currentUserService.getCurrentUser()).thenReturn(mockCurrentUser);

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
                LocalDateTime ldt2 = LocalDateTime.parse("2023-01-03T00:00:00");

                MenuItemReview menuItemReviewOrig = MenuItemReview.builder()
                                .id(1L)
                                .studentUserId(100L)
                                .menuItem(mockMenuItem)
                                .itemServedDate(LocalDateTime.parse("2024-11-24T09:00:00"))
                                .status("Awaiting Moderation")
                                .rating(5)
                                .reviewText("Great food!")
                                .createdDate(ldt1)
                                .lastEditedDate(ldt1)
                                .build();

                MenuItemReview menuItemReviewEdited = MenuItemReview.builder()
                                .id(1L)
                                .studentUserId(100L)
                                .menuItem(mockMenuItem)
                                .itemServedDate(LocalDateTime.parse("2024-11-24T09:00:00"))
                                .status("Awaiting Moderation")
                                .rating(4)
                                .reviewText("Good food.")
                                .createdDate(ldt1)
                                .lastEditedDate(now)
                                .build();

                String requestBody = mapper.writeValueAsString(menuItemReviewEdited);

                when(menuItemReviewRepository.findById(eq(1L))).thenReturn(Optional.of(menuItemReviewEdited));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/menuitemreviews?id=1")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(menuItemReviewRepository, times(1)).findById(1L);
                verify(menuItemReviewRepository, times(1)).save(menuItemReviewEdited); // should be saved with correct user

                ArgumentCaptor<MenuItemReview> captor = ArgumentCaptor.forClass(MenuItemReview.class);
                verify(menuItemReviewRepository, times(1)).save(captor.capture());

                MenuItemReview savedReview = captor.getValue();
                assertEquals(4, savedReview.getRating());
                assertEquals("Good food.", savedReview.getReviewText());
                assertNotNull(savedReview.getLastEditedDate());

        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_review_that_does_not_exist() throws Exception {
                // arrange
                MenuItem mockMenuItem = new MenuItem();
                when(menuItemRepository.findById(1L)).thenReturn(Optional.of(mockMenuItem));

                User mockUser = User.builder().id(100L).build();
                CurrentUser mockCurrentUser = CurrentUser.builder().user(mockUser).build();

                when(currentUserService.getCurrentUser()).thenReturn(mockCurrentUser);

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                MenuItemReview menuItemReviewOrig = MenuItemReview.builder()
                                .id(1L)
                                .studentUserId(100L)
                                .menuItem(mockMenuItem)
                                .itemServedDate(LocalDateTime.parse("2024-11-24T09:00:00"))
                                .status("Awaiting Moderation")
                                .rating(5)
                                .reviewText("Great food!")
                                .createdDate(ldt1)
                                .lastEditedDate(ldt1)
                                .build();

                String requestBody = mapper.writeValueAsString(menuItemReviewOrig);

                when(menuItemReviewRepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/menuitemreviews?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(menuItemReviewRepository, times(1)).findById(67L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("MenuItemReview with id 67 not found", json.get("message"));

        }


}