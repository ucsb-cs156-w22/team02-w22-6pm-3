package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.UCSBRequirement;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.UCSBRequirementRepository;

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


    // Authorization tests for /api/UCSBRequirements/all

    @Test
    public void api_UCSBRequirements_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirements_all__user_logged_in__returns_200() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/all"))
                .andExpect(status().isOk());
    }

    // Authorization tests for /api/UCSBRequirements/post

    @Test
    public void api_UCSBRequirements_post__logged_out__returns_403() throws Exception {
        mockMvc.perform(post("/api/UCSBRequirements/post"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirements_post__user_logged_in() throws Exception {
        // arrange

        //User u = currentUserService.getCurrentUser().getUser();

        UCSBRequirement expectedUCSBRequirement = UCSBRequirement.builder()
                .id(0L)
                .requirementCode("Test RequirementCode")
                .requirementTranslation("Test RequirementTranslation")
                .collegeCode("Test CollegeCode")
                .objCode("Test ObjCode")
                .courseCount(1)
                .units(4)
                .inactive(true)
                .build();

        when(ucsbRequirementRepository.save(eq(expectedUCSBRequirement))).thenReturn(expectedUCSBRequirement);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/UCSBRequirements/post?requirementCode=Test RequirementCode&requirementTranslation=Test RequirementTranslation&collegeCode=Test CollegeCode&objCode=Test ObjCode&courseCount=1&units=4&inactive=true")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(ucsbRequirementRepository, times(1)).save(expectedUCSBRequirement);
        String expectedJson = mapper.writeValueAsString(expectedUCSBRequirement);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirement_all__user_logged_in__returns_only_UCSBRequirement_for_user() throws Exception {

        // arrange

        User thisUser = currentUserService.getCurrentUser().getUser();

        UCSBRequirement req1 = UCSBRequirement.builder().requirementCode("test").requirementTranslation("test").collegeCode("test").objCode("test").courseCount(4).units(4).inactive(true).id(1L).build();
        UCSBRequirement req2 = UCSBRequirement.builder().requirementCode("test2").requirementTranslation("test2").collegeCode("test2").objCode("test2").courseCount(4).units(4).inactive(true).id(2L).build();

        ArrayList<UCSBRequirement> expectedRequirements = new ArrayList<>();
        expectedRequirements.addAll(Arrays.asList(req1, req2));
        when(ucsbRequirementRepository.findAll()).thenReturn(expectedRequirements);

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBRequirements/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(ucsbRequirementRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedRequirements);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

}