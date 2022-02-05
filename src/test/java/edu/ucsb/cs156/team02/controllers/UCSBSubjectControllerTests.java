package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.UCSBSubject;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.UCSBSubjectRepository;

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


@WebMvcTest(controllers = UCSBSubjectController.class)
@Import(TestConfig.class)
public class UCSBSubjectControllerTests extends ControllerTestCase {

     @MockBean
    UCSBSubjectRepository uCSBSubjectRepository;

    @MockBean
    UserRepository userRepository;

    @Test
    public void api_UCSBSubject_admin_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/UCSBSubject/admin/all"))
                .andExpect(status().is(403));
    }



    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubject__user_logged_in__returns_a_UCSBSubject_that_exists() throws Exception {

        // arrange

        User u = currentUserService.getCurrentUser().getUser();
        UCSBSubject ucsbSubject1 = UCSBSubject.builder().subjectCode("UCSBSubject 1").subjectTranslation("UCSBSubject 1").deptCode("UCSBSubject 1").collegeCode("UCSBSubject 1").relatedDeptCode("UCSBSubject 1").inactive(false).id(7L).build();
        when(uCSBSubjectRepository.findById(eq(7L))).thenReturn(Optional.of(ucsbSubject1));

        // act
        MvcResult response = mockMvc.perform(get("/api/ucsb_subjects?id=7"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(uCSBSubjectRepository, times(1)).findById(eq(7L));
        String expectedJson = mapper.writeValueAsString(ucsbSubject1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
}