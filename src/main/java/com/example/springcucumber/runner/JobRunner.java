package com.example.springcucumber.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobRunner implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final Job processTransactionsJob;
    private final Job processStoredProcJob;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting batch jobs");
        
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // Run the original job
        var execution1 = jobLauncher.run(processTransactionsJob, jobParameters);
        log.info("First job completed with status: {}", execution1.getStatus());

        // Run the stored procedure job
        var execution2 = jobLauncher.run(processStoredProcJob, jobParameters);
        log.info("Stored procedure job completed with status: {}", execution2.getStatus());
    }
} 