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

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting batch job");
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        var execution = jobLauncher.run(processTransactionsJob, jobParameters);
        log.info("Job completed with status: {}", execution.getStatus());
    }
} 