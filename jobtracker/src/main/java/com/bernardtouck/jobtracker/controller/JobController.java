package com.bernardtouck.jobtracker.controller;

import com.bernardtouck.jobtracker.dto.JobRequest;
import com.bernardtouck.jobtracker.entity.Job;
import com.bernardtouck.jobtracker.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(jobService.getAllJobs(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<Job> createJob(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(jobService.createJob(userDetails.getUsername(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Job> updateJob(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(jobService.updateJob(userDetails.getUsername(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        jobService.deleteJob(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
