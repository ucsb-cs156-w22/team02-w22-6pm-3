package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.CollegiateSubreddit;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.CollegiateSubredditRepository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = CollegiateSubredditController.class)
@Import(TestConfig.class)
public class CollegiateSubredditControllerTests extends ControllerTestCase {

        @MockBean
        CollegiateSubredditRepository collegiateSubredditRepository;

        @MockBean
        UserRepository userRepository;

        // No authorization tests needed for /api/collegiateSubreddits/admin/all
        // ^ or at least...i think ??????????????

        // Authorization tests for /api/collegiateSubreddits/all

        @Test
        public void api_collegiateSubreddits_all__logged_out__returns_403() throws Exception {
                mockMvc.perform(get("/api/collegiateSubreddits/all"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void api_collegiateSubreddits_all__user_logged_in__returns_200() throws Exception {
                mockMvc.perform(get("/api/collegiateSubreddits/all"))
                                .andExpect(status().isOk());
        }

        // Authorization tests for /api/collegiateSubreddits/post
        // Only users (and admin, I'm assuming) can post
        @Test
        public void api_collegiateSubreddits_post__logged_out__returns_403() throws Exception {
                mockMvc.perform(post("/api/todos/post"))
                                .andExpect(status().is(403));
        }

        // Tests with mocks for database actions

        @WithMockUser(roles = { "USER" })
        @Test
        public void api_collegiateSubreddits_post__user_logged_in() throws Exception {
                // arrange
                CollegiateSubreddit expectedCollegiateSubreddit = CollegiateSubreddit.builder()
                                .name("Test Name")
                                .location("Test Location")
                                .subreddit("Test Subreddit")
                                .id(1L)
                                .build();

                when(collegiateSubredditRepository.save(eq(expectedCollegiateSubreddit)))
                                .thenReturn(expectedCollegiateSubreddit);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/collegiateSubreddits/post?name=Test Name&location=Test Location&subreddit=Test Subreddit")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(collegiateSubredditRepository, times(1)).save(expectedCollegiateSubreddit);
                String expectedJson = mapper.writeValueAsString(expectedCollegiateSubreddit);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void api_todos_all__user_logged_in__returns_only_todos_for_user() throws Exception {

                // arrange

                User thisUser = currentUserService.getCurrentUser().getUser();

                Todo todo1 = Todo.builder().title("Todo 1").details("Todo 1").done(false).user(thisUser).id(1L).build();
                Todo todo2 = Todo.builder().title("Todo 2").details("Todo 2").done(false).user(thisUser).id(2L).build();

                ArrayList<Todo> expectedTodos = new ArrayList<>();
                expectedTodos.addAll(Arrays.asList(todo1, todo2));
                when(todoRepository.findAllByUserId(thisUser.getId())).thenReturn(expectedTodos);

                // act
                MvcResult response = mockMvc.perform(get("/api/todos/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(todoRepository, times(1)).findAllByUserId(eq(thisUser.getId()));
                String expectedJson = mapper.writeValueAsString(expectedTodos);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }
}
