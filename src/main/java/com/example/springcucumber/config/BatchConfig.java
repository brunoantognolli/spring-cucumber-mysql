package com.example.springcucumber.config;

import com.example.springcucumber.entity.DayTransaction;
import com.example.springcucumber.entity.ProcessedDayTransaction;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final EntityManagerFactory entityManagerFactory;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public JpaCursorItemReader<DayTransaction> reader() {
        return new JpaCursorItemReaderBuilder<DayTransaction>()
                .name("dayTransactionReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT t FROM DayTransaction t")
                .build();
    }

    @Bean
    public ProcessingItemProcessor processor() {
        return new ProcessingItemProcessor();
    }

    @Bean
    public JpaItemWriter<ProcessedDayTransaction> writer() {
        return new JpaItemWriterBuilder<ProcessedDayTransaction>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Job processTransactionsJob(Step processTransactionsStep) {
        return new JobBuilder("processTransactionsJob", jobRepository)
                .start(processTransactionsStep)
                .build();
    }

    @Bean
    public Step processTransactionsStep() {
        return new StepBuilder("processTransactionsStep", jobRepository)
                .<DayTransaction, ProcessedDayTransaction>chunk(10, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }
} 