package com.bernardtouck.jobtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.fasterxml.jackson.annotation.JsonIgnore;

@SpringBootApplication
public class JobtrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobtrackerApplication.class, args);
	}

}
