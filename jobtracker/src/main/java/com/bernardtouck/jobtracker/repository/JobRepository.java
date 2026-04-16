package com.bernardtouck.jobtracker.repository;

import com.bernardtouck.jobtracker.entity.Job;
import com.bernardtouck.jobtracker.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByUserId(Long userId);
    Optional<Job> findByIdAndUserId(Long id, Long userId);
}
