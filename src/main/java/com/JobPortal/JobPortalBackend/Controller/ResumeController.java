package com.JobPortal.JobPortalBackend.Controller;

import com.JobPortal.JobPortalBackend.Services.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.ResponseEntity.badRequest;

@RestController
@RequestMapping("/resume")
public class ResumeController {

    private final ResumeService resumeService;

    @Autowired
    ResumeController(ResumeService resumeService){
        this.resumeService=resumeService;
    }

    @GetMapping("/{resumeFileName}")
    public ResponseEntity<byte[]> getResume(@PathVariable String resumeFileName) {
        byte[] data= resumeService.getResume(resumeFileName);
        return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=" + resumeFileName)
                .body(data);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadResume(@RequestParam("file") MultipartFile file) {

        if (!resumeService.validateFile(file)) {
            return badRequest().body("Only PDF and DOCX files are allowed.");
        }

        String response=resumeService.uploadResume(file);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteResume() {
        String response=resumeService.deleteResume();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
