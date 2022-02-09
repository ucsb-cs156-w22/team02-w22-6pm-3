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
                mockMvc.perform(post("/api/collegiateSubreddits/post"))
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
                                //.id(1L)
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
        public void api_collegiateSubreddits_user_logged_in__returns_all_collegesubreddit() throws Exception {

                // arrange

                //User thisUser = currentUserService.getCurrentUser().getUser();

                CollegiateSubreddit colsub1 = CollegiateSubreddit.builder().name("Sub Name 1").location("Sub Location 1").subreddit("Sub 1").id(1L).build();
                CollegiateSubreddit colsub2 = CollegiateSubreddit.builder().name("Sub Name 2").location("Sub Location 2").subreddit("Sub 2").id(2L).build();

                ArrayList<CollegiateSubreddit> expectedCollegiateSubreddits = new ArrayList<>();


                expectedCollegiateSubreddits.addAll(Arrays.asList(colsub1, colsub2));
                when(collegiateSubredditRepository.findAll()).thenReturn(expectedCollegiateSubreddits);

                // act
                MvcResult response = mockMvc.perform(get("/api/collegiateSubreddits/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(collegiateSubredditRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedCollegiateSubreddits);
                String responseString = response.getResponse().getContentAsString();

                
                assertEquals(expectedJson, responseString);
                //assertEquals(1,1);
        }

        //Tests for single endpoint GET

        @WithMockUser(roles = { "USER" })
        @Test
        public void api_collegiateSubreddits__user_logged_in__returns_a_collegiateSubreddit_that_exists() throws Exception {
    
            // arrange
    
            CollegiateSubreddit colsub1 = CollegiateSubreddit.builder().name("Test Name").location("Test Loc").subreddit("Test Sub").id(7L).build();
            when(collegiateSubredditRepository.findById(eq(7L))).thenReturn(Optional.of(colsub1));
    
            // act
            MvcResult response = mockMvc.perform(get("/api/collegiateSubreddits?id=7"))
                    .andExpect(status().isOk()).andReturn();
    
            // assert
    
            verify(collegiateSubredditRepository, times(1)).findById(eq(7L));
            String expectedJson = mapper.writeValueAsString(colsub1);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
        }
    
        @WithMockUser(roles = { "USER" })
        @Test
        public void api_collegiateSubreddits__user_logged_in__search_for_collegeiateSubreddit_that_does_not_exist() throws Exception {
    
            // arrange
    
            User u = currentUserService.getCurrentUser().getUser();
    
            when(collegiateSubredditRepository.findById(eq(7L))).thenReturn(Optional.empty());
    
            // act
            MvcResult response = mockMvc.perform(get("/api/collegiateSubreddits?id=7"))
                    .andExpect(status().isBadRequest()).andReturn();
    
            // assert
    
            verify(collegiateSubredditRepository, times(1)).findById(eq(7L));
            String responseString = response.getResponse().getContentAsString();
            assertEquals("id 7 not found", responseString);
        }


 // PUT(edit) tests

        @WithMockUser(roles = { "USER" })
        @Test
        public void api_collegiateSubreddits__user_logged_in__put_collegiateSubreddit() throws Exception {
        // arrange

        CollegiateSubreddit redditPutTest = CollegiateSubreddit
		.builder().name("College").location("Location").subreddit("Subreddit").id(7L).build();
    

        CollegiateSubreddit updatedCollegiateSubreddit = CollegiateSubreddit
		.builder().name("New College").location("New Location").subreddit("New Subreddit").id(7L).build();
        CollegiateSubreddit correctCollegiateSubreddit = CollegiateSubreddit
		.builder().name("New College").location("New Location").subreddit("New Subreddit").id(7L).build();

        String requestBody = mapper.writeValueAsString(updatedCollegiateSubreddit);
        String expectedReturn = mapper.writeValueAsString(correctCollegiateSubreddit);

        when(collegiateSubredditRepository.findById(eq(7L))).thenReturn(Optional.of(redditPutTest));

        // act
        MvcResult response = mockMvc.perform(put("/api/collegiateSubreddits?id=7")
		        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(collegiateSubredditRepository, times(1)).findById(7L);
        verify(collegiateSubredditRepository, times(1)).save(correctCollegiateSubreddit);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedReturn, responseString);
    }



        @WithMockUser(roles = { "USER" })
        @Test
        public void api_collegiateSubreddits__user_logged_in__cannot_put_CollegiateSubreddit_that_does_not_exist() throws Exception {
        // arrange

	CollegiateSubreddit updatedCollegiateSubreddit = CollegiateSubreddit.builder()
		.name("College").location("Location").subreddit("Subreddit").id(7L).build();

        String requestBody = mapper.writeValueAsString(updatedCollegiateSubreddit);

        when(collegiateSubredditRepository.findById(eq(7L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/collegiateSubreddits?id=7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert

        verify(collegiateSubredditRepository, times(1)).findById(7L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("id 7 not found", responseString);
    }




}
