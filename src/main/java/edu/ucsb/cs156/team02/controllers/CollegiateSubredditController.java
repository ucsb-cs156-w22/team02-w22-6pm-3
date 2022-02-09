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

    @Autowired
    ObjectMapper mapper;

    
        /**
     * This inner class helps us factor out some code for checking
     * whether collegiate subreddits exist
     * along with the error messages pertaining to those situations. It
     * bundles together the state needed for those checks.
     */
    public class CollegiateSubredditOrError {
        Long id;
        CollegiateSubreddit collegiateSubreddit;
        ResponseEntity<String> error;

        public CollegiateSubredditOrError(Long id) {
            this.id = id;
        }
    }

    //GET

    @ApiOperation(value = "List all college subreddits") 
    @PreAuthorize("hasRole('ROLE_USER')") // authorization can be removed
    @GetMapping("/all")
    public Iterable<CollegiateSubreddit> allUsersCollegeSubreddits() {
        loggingService.logMethod();
        Iterable<CollegiateSubreddit> reddits = collegiateSubredditRepository.findAll();
        return reddits;
        //return null;
    }

    //POST

    @ApiOperation(value = "Create a new Collegiate Subreddit")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public CollegiateSubreddit postCollegiateSubreddit(
            @ApiParam("name") @RequestParam String name,
            @ApiParam("location") @RequestParam String location,
            @ApiParam("subreddit") @RequestParam String subreddit) {
        loggingService.logMethod();
        CurrentUser currentUser = getCurrentUser();
        log.info("currentUser={}", currentUser);

        CollegiateSubreddit collegiateSubreddit = new CollegiateSubreddit();
        //collegiateSubreddit.setId(currentUser.getUser().getId());
        //database automatically assigns (does it sequentially)
        collegiateSubreddit.setName(name);
        collegiateSubreddit.setLocation(location);
        collegiateSubreddit.setSubreddit(subreddit);
        CollegiateSubreddit savedcollegiateSubreddit = collegiateSubredditRepository.save(collegiateSubreddit);
        return savedcollegiateSubreddit;
    }

    @ApiOperation(value = "Get a single college subreddit by ID")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<String> getCollegiateSubredditById(
            @ApiParam("id") @RequestParam Long id) throws JsonProcessingException {

        loggingService.logMethod();

        CollegiateSubredditOrError csoe = new CollegiateSubredditOrError(id);

        csoe = doesCollegiateSubredditExist(csoe);
        if (csoe.error != null) {
            return csoe.error;
        }

        String body = mapper.writeValueAsString(csoe.collegiateSubreddit);
        return ResponseEntity.ok().body(body);
    }

    @ApiOperation(value = "Edit a single college subreddit")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("")
    public ResponseEntity<String> putCollegiateSubredditById(
            @ApiParam("id") @RequestParam Long id, 
            @RequestBody @Valid CollegiateSubreddit incomingCollegiateSubreddit) throws JsonProcessingException {
        loggingService.logMethod();

        CollegiateSubredditOrError csoe = new CollegiateSubredditOrError(id);

        csoe = doesCollegiateSubredditExist(csoe);
        if (csoe.error != null) {
            return csoe.error;
        }
 
        //long previousId = csoe.collegiateSubreddit.getId();
        //incomingCollegiateSubreddit.setId(previousId);
        collegiateSubredditRepository.save(incomingCollegiateSubreddit);
        

        String body = mapper.writeValueAsString(incomingCollegiateSubreddit);
        return ResponseEntity.ok().body(body);
    }

    @ApiOperation(value = "Delete a college subreddit")
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("")
    public ResponseEntity<String> deleteCollegeSubreddit(
            @ApiParam("id") @RequestParam Long id) {
        loggingService.logMethod();

        CollegiateSubredditOrError csoe = new CollegiateSubredditOrError(id);

        csoe = doesCollegiateSubredditExist(csoe);
        if (csoe.error != null) {
            return csoe.error;
        }

        collegiateSubredditRepository.deleteById(id);

        return ResponseEntity.ok().body(String.format("id %d deleted", id));

    }

            /**
     * Pre-conditions: csoe.id is value to look up, csoe.collegiateSubreddit and csoe.error are null
     * 
     * Post-condition: if collegeSubreddit with id csoe.id exists, csoe.collegeSubreddit now refers to it, and
     * error is null.
     * Otherwise, collegeSubreddit with id csoe.id does not exist, and error is a suitable return
     * value to
     * report this error condition.
     */
    public CollegiateSubredditOrError doesCollegiateSubredditExist(CollegiateSubredditOrError csoe) {

        Optional<CollegiateSubreddit> optionalCollegiateSubreddit = collegiateSubredditRepository.findById(csoe.id);

        if (optionalCollegiateSubreddit.isEmpty()) {
            csoe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("id %d not found", csoe.id));
        } else {
            csoe.collegiateSubreddit = optionalCollegiateSubreddit.get();
        }
        return csoe;
    }
    
}
