package com.JobPortal.JobPortalBackend.Controller;

import com.JobPortal.JobPortalBackend.DTO.ApplicationDTO;
import com.JobPortal.JobPortalBackend.Services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/applications")
public class ApplicationsController {

    private final ApplicationService applicationService;

    @Autowired
    ApplicationsController(ApplicationService applicationService){

        this.applicationService=applicationService;
    }



    @GetMapping("")
    public Page<ApplicationDTO> getMyApplications(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                 @RequestParam(required = false, defaultValue = "10") int pageSize)
    {
        Pageable pageable= PageRequest.of(pageNo-1,pageSize);
        return applicationService.getMyApplications(pageable);
    }


    @GetMapping("/{jobId}")
    public Page<ApplicationDTO> getApplicantsList(@PathVariable("jobId") String jobId,
                                                  @RequestParam(required = false, defaultValue = "1") int pageNo,
                                                  @RequestParam(required = false, defaultValue = "10") int pageSize)
    {
        Pageable pageable= PageRequest.of(pageNo-1,pageSize);
        return applicationService.getApplicantsList(jobId, pageable);
    }

    @PostMapping("/{jobPostId}/apply")
    public ResponseEntity<String> applyForJob(@PathVariable String jobPostId){
        return applicationService.applyForJob(jobPostId);
    }

    @DeleteMapping("/{applicationId}")
    public ResponseEntity<String> deleteApplication(@PathVariable String applicationId){
        return applicationService.deleteApplication(applicationId);
    }

}
