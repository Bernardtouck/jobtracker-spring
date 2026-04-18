package com.bernardtouck.jobtracker.service;

import com.bernardtouck.jobtracker.dto.JobRequest;
import com.bernardtouck.jobtracker.entity.Job;
import com.bernardtouck.jobtracker.entity.JobStatus;
import com.bernardtouck.jobtracker.entity.User;
import com.bernardtouck.jobtracker.repository.JobRepository;
import com.bernardtouck.jobtracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JobService jobService;

    private User mockUser;
    private Job mockJob;
    private JobRequest jobRequest;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@test.com");

        mockJob = new Job();
        mockJob.setId(1L);
        mockJob.setCompany("Stripe");
        mockJob.setPosition("Backend Engineer");
        mockJob.setStatus(JobStatus.APPLIED);
        mockJob.setUser(mockUser);

        jobRequest = new JobRequest();
        jobRequest.setCompany("Stripe");
        jobRequest.setPosition("Backend Engineer");
        jobRequest.setStatus(JobStatus.APPLIED);
        jobRequest.setLocation("Berlin");
    }

    // ─── TEST 1 : Récupérer tous les jobs ─────────────────────
    @Test
    void getAllJobs_ShouldReturnUserJobs() {
        // Arrange
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));
        when(jobRepository.findByUserId(1L)).thenReturn(List.of(mockJob));

        // Act
        List<Job> result = jobService.getAllJobs("test@test.com");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Stripe", result.get(0).getCompany());
    }

    // ─── TEST 2 : Créer un job ────────────────────────────────
    @Test
    void createJob_ShouldReturnSavedJob() {
        // Arrange
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));
        when(jobRepository.save(any(Job.class))).thenReturn(mockJob);

        // Act
        Job result = jobService.createJob("test@test.com", jobRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Stripe", result.getCompany());
        assertEquals(JobStatus.APPLIED, result.getStatus());
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    // ─── TEST 3 : Supprimer un job ───────────────────────────
    @Test
    void deleteJob_ShouldCallDelete_WhenJobBelongsToUser() {
        // Arrange
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));
        when(jobRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mockJob));

        // Act
        jobService.deleteJob("test@test.com", 1L);

        // Assert
        verify(jobRepository, times(1)).delete(mockJob);
    }

    // ─── TEST 4 : Supprimer un job qui n'existe pas ──────────
    @Test
    void deleteJob_ShouldThrowException_WhenJobNotFound() {
        // Arrange
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));
        when(jobRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> jobService.deleteJob("test@test.com", 99L));

        verify(jobRepository, never()).delete(any(Job.class));
    }

    // ─── TEST 5 : Modifier un job ─────────────────────────
    @Test
    void updateJob_ShouldReturnUpdatedJob_WhenJobExists() {
        // Arrange
        JobRequest updateRequest = new JobRequest();
        updateRequest.setCompany("Google");
        updateRequest.setPosition("Senior Engineer");
        updateRequest.setStatus(JobStatus.INTERVIEW);

        Job updatedJob = new Job();
        updatedJob.setId(1L);
        updatedJob.setCompany("Google");
        updatedJob.setPosition("Senior Engineer");
        updatedJob.setStatus(JobStatus.INTERVIEW);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));
        when(jobRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mockJob));
        when(jobRepository.save(any(Job.class))).thenReturn(updatedJob);

        // Act
        Job result = jobService.updateJob("test@test.com", 1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Google", result.getCompany());
        assertEquals(JobStatus.INTERVIEW, result.getStatus());
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    // ─── TEST 6 : Modifier un job inexistant ──────────────
    @Test
    void updateJob_ShouldThrowException_WhenJobNotFound() {
        // Arrange
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));
        when(jobRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> jobService.updateJob("test@test.com", 99L, jobRequest));

        verify(jobRepository, never()).save(any(Job.class));
    }
}