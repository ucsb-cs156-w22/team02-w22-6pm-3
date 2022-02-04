package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.CollegiateSubreddit;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.repositories.CollegiateSubredditRepository;
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

@Api(description = "Collegiate Subreddits")
@RequestMapping("/api/collegiateSubreddits")
@RestController
@Slf4j
public class CollegiateSubredditController extends ApiController {


    @Autowired
    CollegiateSubredditRepository collegiateSubredditRepository;


    @ApiOperation(value = "List all college subreddits") //
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/all")
    public Iterable<CollegiateSubreddit> allUsersCollegeSubreddits() {
        loggingService.logMethod();
        Iterable<CollegiateSubreddit> reddits = collegiateSubredditRepository.findAll();
        return reddits;
    }

    @ApiOperation(value = "List this user's todos") // ??
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<CollegiateSubreddit> thisUsersCollegeSubreddits() {
        loggingService.logMethod();
        CurrentUser currentUser = getCurrentUser();
        Iterable<CollegiateSubreddit> reddits = collegiateSubredditRepository.findAllByUserId(currentUser.getUser().getId());
        //^ not sure what function i need to replace
        return reddits;
    }
}
