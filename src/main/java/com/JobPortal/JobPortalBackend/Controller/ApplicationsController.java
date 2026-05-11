package com.JobPortal.JobPortalBackend.Controller;

import com.JobPortal.JobPortalBackend.DTO.ApplicationDTO;
import com.JobPortal.JobPortalBackend.Services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/applications")
public class ApplicationsController {

    private final ApplicationService applicationService;

    @Autowired
    ApplicationsController(ApplicationService applicationService){

        this.applicationService=applicationService;
    }



    @GetMapping("/my_applications")
    public Page<ApplicationDTO> getMyApplications(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                 @RequestParam(required = false, defaultValue = "10") int pageSize)
    {
        Pageable pageable= PageRequest.of(pageNo-1,pageSize);
        return applicationService.getMyApplications(pageable);
    }


    @GetMapping("/{jobId}")
    public Page<ApplicationDTO> getApplicantsList(@PathVariable("jobId") UUID jobId,
                                                  @RequestParam(required = false, defaultValue = "1") int pageNo,
                                                  @RequestParam(required = false, defaultValue = "10") int pageSize)
    {
        Pageable pageable= PageRequest.of(pageNo-1,pageSize);
        return applicationService.getApplicantsList(jobId, pageable);
    }

    @PostMapping("/{jobPostId}/apply")
    public ResponseEntity<String> applyForJob(@PathVariable UUID jobPostId){
        return applicationService.applyForJob(jobPostId);
    }

    @PutMapping("/cancel/{applicationId}")
    public ResponseEntity<String> cancelApplication(@PathVariable UUID applicationId){
        return applicationService.cancelApplication(applicationId);
    }

    @PutMapping("/shortlist/{applicationId}")
    public ResponseEntity<?> shortlistApplication(@PathVariable UUID applicationId){
        return applicationService.shortlistApplication(applicationId);

    }

    @PutMapping("/reject/{applicationId}")
    public ResponseEntity<?> rejectApplication(@PathVariable UUID applicationId){
        return applicationService.rejectApplication(applicationId);

    }

}
