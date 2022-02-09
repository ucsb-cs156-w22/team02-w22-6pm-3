package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.UCSBRequirement;
import edu.ucsb.cs156.team02.repositories.UCSBRequirementRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@Api(description = "UCSB Requirements")
@RequestMapping("/api/UCSBRequirements")
@RestController
public class UCSBRequirementController extends ApiController {

    public class UCSBRequirementOrError {
        Long id;
        UCSBRequirement req;
        ResponseEntity<String> error;

        public UCSBRequirementOrError(Long id) {
            this.id = id;
        }
    }

    @Autowired
    UCSBRequirementRepository ucsbRequirementRepository;

    @Autowired
    ObjectMapper mapper;
    // Added this in previous task by accident
    @ApiOperation(value = "Get a single requirement by ID")
    // @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<String> getUCSBRequirementById(
            @ApiParam("id") @RequestParam Long id) throws JsonProcessingException {
        loggingService.logMethod();
        UCSBRequirementOrError roe = new UCSBRequirementOrError(id);

        roe = doesUCSBRequirementExist(roe);
        if (roe.error != null) {
            return roe.error;
        }
        
        String body = mapper.writeValueAsString(roe.req);
        return ResponseEntity.ok().body(body);
    }

    @ApiOperation(value = "List all requirements")
    @GetMapping("/all")
    public Iterable<UCSBRequirement> allUCSBRequirements() {
        loggingService.logMethod();
        Iterable<UCSBRequirement> reqs = ucsbRequirementRepository.findAll();
        return reqs;
    }

    @ApiOperation(value = "Create a new requirement")
    @PostMapping("/post")
    public UCSBRequirement postUcsbRequirement(
            @ApiParam("requirementCode") @RequestParam String requirementCode,
            @ApiParam("requirementTranslation") @RequestParam String requirementTranslation,
            @ApiParam("collegeCode") @RequestParam String collegeCode,
            @ApiParam("objCode") @RequestParam String objCode,
            @ApiParam("courseCount") @RequestParam int courseCount,
            @ApiParam("units") @RequestParam int units,
            @ApiParam("inactive") @RequestParam Boolean inactive,
            @ApiParam("id") @RequestParam long id) {
        loggingService.logMethod();

        UCSBRequirement req = new UCSBRequirement();
        req.setRequirementCode(requirementCode);
        req.setRequirementTranslation(requirementTranslation);
        req.setCollegeCode(collegeCode);
        req.setObjCode(objCode);
        req.setCourseCount(courseCount);
        req.setUnits(units);
        req.setInactive(inactive);
        req.setId(id);
        
        UCSBRequirement savedReq = ucsbRequirementRepository.save(req);
        return savedReq;
    }
    
    @ApiOperation(value = "Update a single requirement by ID")
    @PutMapping("")
    public ResponseEntity<String> putTodoById(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid UCSBRequirement incomingUCSBRequirement) throws JsonProcessingException {
        loggingService.logMethod();

        UCSBRequirementOrError roe = new UCSBRequirementOrError(id);

        roe = doesUCSBRequirementExist(roe);
        if (roe.error != null) {
            return roe.error;
        }

        ucsbRequirementRepository.save(incomingUCSBRequirement);

        String body = mapper.writeValueAsString(incomingUCSBRequirement);
        return ResponseEntity.ok().body(body);
    }

    public UCSBRequirementOrError doesUCSBRequirementExist(UCSBRequirementOrError roe) {

        Optional<UCSBRequirement> optionalReq = ucsbRequirementRepository.findById(roe.id);

        if (optionalReq.isEmpty()) {
            roe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("requirement with id %d not found", roe.id));
        } else {
            roe.req = optionalReq.get();
        }
        return roe;
    }

    @ApiOperation(value = "Delete a requirement by ID")
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("")
    public ResponseEntity<String> deleteUCSBRequirement(
            @ApiParam("id") @RequestParam Long id) {
        loggingService.logMethod();

        UCSBRequirementOrError toe = new UCSBRequirementOrError(id);

        toe = doesUCSBRequirementExist(toe);
        if (toe.error != null) {
            return toe.error;
        }

        ucsbRequirementRepository.deleteById(id);
        return ResponseEntity.ok().body(String.format("requirement with id %d deleted", id));

    } 

}