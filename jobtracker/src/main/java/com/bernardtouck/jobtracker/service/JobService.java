package com.bernardtouck.jobtracker.service;

import com.bernardtouck.jobtracker.dto.JobRequest;
import com.bernardtouck.jobtracker.entity.Job;
import com.bernardtouck.jobtracker.entity.User;
import com.bernardtouck.jobtracker.repository.JobRepository;
import com.bernardtouck.jobtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public List<Job> getAllJobs(String email) {
        User user = getUser(email);
        return jobRepository.findByUserId(user.getId());
    }

    public Job createJob(String email, JobRequest request) {
        User user = getUser(email);

        Job job = new Job();
        job.setCompany(request.getCompany());
        job.setPosition(request.getPosition());
        job.setStatus(request.getStatus());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setContractType(request.getContractType());
        job.setWorkMode(request.getWorkMode());
        job.setNotes(request.getNotes());
        job.setUser(user);

        return jobRepository.save(job);
    }

    public Job updateJob(String email, Long jobId, JobRequest request) {
        User user = getUser(email);

        Job job = jobRepository.findByIdAndUserId(jobId, user.getId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        job.setCompany(request.getCompany());
        job.setPosition(request.getPosition());
        job.setStatus(request.getStatus());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setContractType(request.getContractType());
        job.setWorkMode(request.getWorkMode());
        job.setNotes(request.getNotes());

        return jobRepository.save(job);
    }

    public void deleteJob(String email, Long jobId) {
        User user = getUser(email);

        Job job = jobRepository.findByIdAndUserId(jobId, user.getId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        jobRepository.delete(job);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
