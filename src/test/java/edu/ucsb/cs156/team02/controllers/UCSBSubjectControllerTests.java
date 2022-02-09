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
        mockMvc.perform(get("/api/UCSBSubjects/all"))
                .andExpect(status().is(403));
    }


    
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_ucsbsubject__user_logged_in__returns_a_UCSBSubject_that_exists() throws Exception {

        // arrange

        User u = currentUserService.getCurrentUser().getUser();
        UCSBSubject ucsbSubject1 = UCSBSubject.builder().subjectCode("UCSBSubject 1").subjectTranslation("UCSBSubject 1").deptCode("UCSBSubject 1").collegeCode("UCSBSubject 1").relatedDeptCode("UCSBSubject 1").inactive(false).id(7L).build();
        when(uCSBSubjectRepository.findById(eq(7L))).thenReturn(Optional.of(ucsbSubject1));

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBSubjects?id=7"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(uCSBSubjectRepository, times(1)).findById(eq(7L));
        String expectedJson = mapper.writeValueAsString(ucsbSubject1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
     //maybe unlimplemented yet?

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubject_post__user_logged_in() throws Exception {
        // arrange

        UCSBSubject expectedUCSBSubject = UCSBSubject.builder().subjectCode("Test_SubjectCode").deptCode("Test_DeptCode").relatedDeptCode("Test_RelatedDeptCode").collegeCode("Test_CollegeCode").inactive(true).subjectTranslation("Test_SubjectTranslation").id(0L).build();
                
                
                

        when(uCSBSubjectRepository.save(eq(expectedUCSBSubject))).thenReturn(expectedUCSBSubject);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/UCSBSubjects/post?subjectCode=Test_SubjectCode&deptCode=Test_DeptCode&subjectTranslation=Test_SubjectTranslation&inactive=true&collegeCode=Test_CollegeCode&relatedDeptCode=Test_RelatedDeptCode")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(uCSBSubjectRepository, times(1)).save(expectedUCSBSubject);
        String expectedJson = mapper.writeValueAsString(expectedUCSBSubject);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_todos_admin_all__admin_logged_in__returns_all_todos() throws Exception {

        // arrange
        /*
        User u1 = User.builder().id(1L).build();
        User u2 = User.builder().id(2L).build();
        User u = currentUserService.getCurrentUser().getUser();
        */

        UCSBSubject ucsbSubject1 = UCSBSubject.builder().subjectCode("UCSBSubject 1").deptCode("UCSBSubject 1").relatedDeptCode("UCSBSubject 1").collegeCode("UCSBSubject 1").id(1L).inactive(true).subjectTranslation("UCSBSubject 1").build();
        UCSBSubject ucsbSubject2 = UCSBSubject.builder().subjectCode("UCSBSubject 2").deptCode("UCSBSubject 2").relatedDeptCode("UCSBSubject 2").collegeCode("UCSBSubject 2").id(2L).inactive(true).subjectTranslation("UCSBSubject 2").build();
        UCSBSubject ucsbSubject3 = UCSBSubject.builder().subjectCode("UCSBSubject 3").deptCode("UCSBSubject 3").relatedDeptCode("UCSBSubject 3").collegeCode("UCSBSubject 3").id(1L).inactive(true).subjectTranslation("UCSBSubject 3").build();

        ArrayList<UCSBSubject> expectedSubjects = new ArrayList<>();
        expectedSubjects.addAll(Arrays.asList(ucsbSubject1, ucsbSubject2, ucsbSubject3));

        when(uCSBSubjectRepository.findAll()).thenReturn(expectedSubjects);

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBSubjects/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(uCSBSubjectRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedSubjects);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

        
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubjects__user_logged_in__search_for_todo_that_does_not_exist() throws Exception {

        // arrange

        User u = currentUserService.getCurrentUser().getUser();

        when(uCSBSubjectRepository.findById(eq(7L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBSubjects?id=7"))
                .andExpect(status().isBadRequest()).andReturn();

        // assert

        verify(uCSBSubjectRepository, times(1)).findById(eq(7L));
        String responseString = response.getResponse().getContentAsString();
        assertEquals("id 7 not found", responseString);
    }

     @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubject__user_logged_in__delete_todo_that_does_not_exist() throws Exception {
        // arrange

        User otherUser = User.builder().id(98L).build();
        UCSBSubject ucsbSubject1 = UCSBSubject.builder().subjectCode("UCSBSubject 1").subjectTranslation("UCSBSubject 1").deptCode("UCSBSubject 1").collegeCode("UCSBSubject 1").relatedDeptCode("UCSBSubject 1").inactive(false).id(15L).build();
        when(uCSBSubjectRepository.findById(eq(15L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/UCSBSubjects?id=15")
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(uCSBSubjectRepository, times(1)).findById(15L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("record 15 not found", responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubject__user_logged_in__delete_subject() throws Exception {
        // arrange

        User otherUser = User.builder().id(98L).build();
        UCSBSubject ucsbSubject1 = UCSBSubject.builder().subjectCode("UCSBSubject 1").subjectTranslation("UCSBSubject 1").deptCode("UCSBSubject 1").collegeCode("UCSBSubject 1").relatedDeptCode("UCSBSubject 1").inactive(false).id(16L).build();
        when(uCSBSubjectRepository.findById(eq(16L))).thenReturn(Optional.of(ucsbSubject1));

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/UCSBSubjects?id=16")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(uCSBSubjectRepository, times(1)).findById(16L);
        verify(uCSBSubjectRepository, times(1)).deleteById(16L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("record 16 deleted", responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubject__user_logged_in__put_subject() throws Exception {
        // arrange

        User u = currentUserService.getCurrentUser().getUser();
        User otherUser = User.builder().id(999).build();
        UCSBSubject ucsbSubject1 = UCSBSubject.builder().subjectCode("UCSBSubject 1").subjectTranslation("UCSBSubject 1").deptCode("UCSBSubject 1").collegeCode("UCSBSubject 1").relatedDeptCode("UCSBSubject 1").inactive(false).id(67L).build();
        // We deliberately set the user information to another user
        // This shoudl get ignored and overwritten with currrent user when todo is saved

        UCSBSubject updatedSubject = UCSBSubject.builder().subjectCode("UCSBSubject 2").subjectTranslation("UCSBSubject 2").deptCode("UCSBSubject 2").collegeCode("UCSBSubject 2").relatedDeptCode("UCSBSubject 2").inactive(false).id(67L).build();
        UCSBSubject correctSubject = UCSBSubject.builder().subjectCode("UCSBSubject 2").subjectTranslation("UCSBSubject 2").deptCode("UCSBSubject 2").collegeCode("UCSBSubject 2").relatedDeptCode("UCSBSubject 2").inactive(false).id(67L).build();

        String requestBody = mapper.writeValueAsString(updatedSubject);
        String expectedReturn = mapper.writeValueAsString(correctSubject);

        when(uCSBSubjectRepository.findById(eq(67L))).thenReturn(Optional.of(ucsbSubject1));

        // act
        MvcResult response = mockMvc.perform(
                put("/api/UCSBSubjects?id=67")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(uCSBSubjectRepository, times(1)).findById(67L);
        verify(uCSBSubjectRepository, times(1)).save(correctSubject); // should be saved with correct user
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedReturn, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubject__user_logged_in__cannot_put_subject_that_does_not_exist() throws Exception {
        // arrange

        UCSBSubject updatedSubject = UCSBSubject.builder().subjectCode("UCSBSubject 2").subjectTranslation("UCSBSubject 2").deptCode("UCSBSubject 2").collegeCode("UCSBSubject 2").relatedDeptCode("UCSBSubject 2").inactive(false).id(67L).build();

        String requestBody = mapper.writeValueAsString(updatedSubject);

        when(uCSBSubjectRepository.findById(eq(67L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/UCSBSubjects?id=67")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(uCSBSubjectRepository, times(1)).findById(67L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("id 67 not found", responseString);
    }
    

    
}