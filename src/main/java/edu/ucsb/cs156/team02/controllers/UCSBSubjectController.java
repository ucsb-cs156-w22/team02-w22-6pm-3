package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.UCSBSubject;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.repositories.UCSBSubjectRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;




@Api(description = "UCSBSubjects")
@RequestMapping("/api/UCSBSubjects/")
@RestController
@Slf4j
public class UCSBSubjectController extends ApiController {

    public class UCSBSubjectOrError {
        Long id;
        UCSBSubject uCSBSubject;
        ResponseEntity<String> error;
        public UCSBSubjectOrError(Long id) {
        this.id = id;
    }
    }

    @Autowired
    UCSBSubjectRepository uCSBSubjectRepository;


    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "List all UCSB subjects")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public Iterable<UCSBSubject> allUCSBSubjects() {
        loggingService.logMethod();
        Iterable<UCSBSubject> subjects = uCSBSubjectRepository.findAll();
        return subjects;
    }


    @ApiOperation(value = "Create a new UCSB subject")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBSubject postUCSBSubject(
            @ApiParam("subjectCode") @RequestParam String subjectCode,
            @ApiParam("subjectTranslation") @RequestParam String subjectTranslation,
            @ApiParam("deptCode") @RequestParam String deptCode,
            @ApiParam("collegeCode") @RequestParam String collegeCode,
            @ApiParam("relatedDeptCode") @RequestParam String relatedDeptCode,
            @ApiParam("inactive") @RequestParam Boolean inactive) {
        loggingService.logMethod();
        CurrentUser currentUser = getCurrentUser();
        log.info("currentUser={}", currentUser);

        UCSBSubject uCSBSubject = new UCSBSubject();
        uCSBSubject.setSubjectCode(subjectCode);
        uCSBSubject.setSubjectTranslation(subjectTranslation);
        uCSBSubject.setDeptCode(deptCode);
        uCSBSubject.setCollegeCode(collegeCode);
        uCSBSubject.setRelatedDeptCode(relatedDeptCode);
        uCSBSubject.setInactive(inactive);
        UCSBSubject savedUCSBSubject = uCSBSubjectRepository.save(uCSBSubject);
        return savedUCSBSubject;
    }


    
    @ApiOperation(value = "Get record of UCSB Subject with id")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("")
    public ResponseEntity<String> getUCSBSubjectById123_admin(
            @ApiParam("id") @RequestParam Long id) throws JsonProcessingException {
        loggingService.logMethod();

        UCSBSubjectOrError toe = new UCSBSubjectOrError(id);

        toe = doesUCSBSubjectExist(toe);
        if (toe.error != null) {
            return toe.error;
        }

        String body = mapper.writeValueAsString(toe.uCSBSubject);
        return ResponseEntity.ok().body(body);
    }

    public UCSBSubjectOrError doesUCSBSubjectExist(UCSBSubjectOrError toe) {

        Optional<UCSBSubject> optionalUCSBSubject = uCSBSubjectRepository.findById(toe.id);

        if (optionalUCSBSubject.isEmpty()) {
            toe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("subject with id %d not found", toe.id));
        } else {
            toe.uCSBSubject = optionalUCSBSubject.get();
        }
        return toe;
    }
    

    

    
}