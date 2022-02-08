package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UCSBRequirementRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.UCSBRequirement;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.UserRepository;

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

@WebMvcTest(controllers = UCSBRequirementController.class)
@Import(TestConfig.class)
public class UCSBRequirementControllerTests extends ControllerTestCase {

    @MockBean
    UCSBRequirementRepository ucsbRequirementRepository;

    @MockBean
    UserRepository userRepository;

    // Tests with mocks for database actions

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_reqs__user_logged_in__returns_a_req_that_exists() throws Exception {

        // arrange

        User u = currentUserService.getCurrentUser().getUser();
        UCSBRequirement req1 = UCSBRequirement.builder()
                .requirementCode("X")
                .requirementTranslation("X")
                .collegeCode("X")
                .objCode("X")
                .courseCount(0)
                .units(0)
                .inactive(false)
                .id(7L).build();

        when(ucsbRequirementRepository.findById(eq(7L))).thenReturn(Optional.of(req1));

        // act

        MvcResult response = mockMvc.perform(get("/api/UCSBRequirements?id=7"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(ucsbRequirementRepository, times(1)).findById(eq(7L));
        String expectedJson = mapper.writeValueAsString(req1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_reqs__user_logged_in__search_for_req_that_does_not_exist() throws Exception {

        // arrange

        User u = currentUserService.getCurrentUser().getUser();

        when(ucsbRequirementRepository.findById(eq(7L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBRequirements?id=7"))
                .andExpect(status().isBadRequest()).andReturn();

        // assert

        verify(ucsbRequirementRepository, times(1)).findById(eq(7L));
        String responseString = response.getResponse().getContentAsString();
        assertEquals("requirement with id 7 not found", responseString);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_reqs__admin_logged_in__returns_a_req_that_exists() throws Exception {

        // arrange

        UCSBRequirement req1 = UCSBRequirement.builder()
                .requirementCode("X")
                .requirementTranslation("X")
                .collegeCode("X")
                .objCode("X")
                .courseCount(0)
                .units(0)
                .inactive(false)
                .id(7L).build();

        when(ucsbRequirementRepository.findById(eq(7L))).thenReturn(Optional.of(req1));

        // act

        MvcResult response = mockMvc.perform(get("/api/UCSBRequirements?id=7"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(ucsbRequirementRepository, times(1)).findById(eq(7L));
        String expectedJson = mapper.writeValueAsString(req1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_req__admin_logged_in__search_for_req_that_does_not_exist() throws Exception {

        // arrange

        when(ucsbRequirementRepository.findById(eq(29L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBRequirements?id=29"))
                .andExpect(status().isBadRequest()).andReturn();

        // assert

        verify(ucsbRequirementRepository, times(1)).findById(eq(29L));
        String responseString = response.getResponse().getContentAsString();
        assertEquals("requirement with id 29 not found", responseString);
    }



    @Test
    public void api_reqs_all__returns_all_reqs() throws Exception {

        // arrange

        UCSBRequirement req1 = UCSBRequirement.builder()
                .requirementCode("A")
                .requirementTranslation("A")
                .collegeCode("A")
                .objCode("A")
                .courseCount(1)
                .units(1)
                .inactive(false)
                .id(1L).build();

        UCSBRequirement req2 = UCSBRequirement.builder()
                .requirementCode("B")
                .requirementTranslation("B")
                .collegeCode("B")
                .objCode("B")
                .courseCount(2)
                .units(2)
                .inactive(false)
                .id(2L).build();

        UCSBRequirement req3 = UCSBRequirement.builder()
                .requirementCode("C")
                .requirementTranslation("C")
                .collegeCode("C")
                .objCode("C")
                .courseCount(3)
                .units(3)
                .inactive(false)
                .id(3L).build();

        ArrayList<UCSBRequirement> expectedReqs = new ArrayList<>();
        expectedReqs.addAll(Arrays.asList(req1, req2, req3));

        when(ucsbRequirementRepository.findAll()).thenReturn(expectedReqs);

        // act

        MvcResult response = mockMvc.perform(get("/api/UCSBRequirements/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(ucsbRequirementRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedReqs);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @Test
    public void api_reqs_post() throws Exception {
        
        // arrange

        UCSBRequirement expectedReq = UCSBRequirement.builder()
                .requirementCode("A")
                .requirementTranslation("B")
                .collegeCode("C")
                .objCode("D")
                .courseCount(100)
                .units(100)
                .inactive(true)
                .id(42L).build();

        when(ucsbRequirementRepository.save(eq(expectedReq))).thenReturn(expectedReq);

        // act

        MvcResult response = mockMvc.perform(
                post("/api/UCSBRequirements/post?requirementCode=A&requirementTranslation=B&collegeCode=C&objCode=D&courseCount=100&units=100&inactive=true&id=42")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(ucsbRequirementRepository, times(1)).save(expectedReq);
        String expectedJson = mapper.writeValueAsString(expectedReq);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

}