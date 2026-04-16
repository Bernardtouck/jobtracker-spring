package com.bernardtouck.jobtracker.dto;

import com.bernardtouck.jobtracker.entity.JobStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobRequest {
    @NotBlank
    private String company;

    @NotBlank
    private String position;

    private JobStatus status = JobStatus.APPLIED;
    private String location;
    private String salary;
    private String contractType;
    private String workMode;
    private String notes;
}
