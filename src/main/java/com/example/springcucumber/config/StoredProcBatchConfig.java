package com.example.springcucumber.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StreamUtils;

import com.example.springcucumber.entity.TransactionProcedureResult;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StoredProcBatchConfig {

    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JdbcTemplate jdbcTemplate;

    private String loadSqlFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("storeProc.sql");
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }
    @Bean
    public JdbcCursorItemReader<TransactionProcedureResult> storedProcReader() throws IOException {
        // First, execute the SQL script to create temporary tables
        String fullSql = loadSqlFile();
        jdbcTemplate.execute(fullSql);

        return new JdbcCursorItemReaderBuilder<TransactionProcedureResult>()
                .name("storedProcReader")
                .dataSource(dataSource)
                .sql("CALL ProcessDayTransactions()")
                .rowMapper(new BeanPropertyRowMapper<>(TransactionProcedureResult.class))
                .build();
    }

    @Bean
    public JpaItemWriter<TransactionProcedureResult> storedProcWriter() {
        return new JpaItemWriterBuilder<TransactionProcedureResult>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public ItemProcessor<TransactionProcedureResult, TransactionProcedureResult> storedProcProcessor() {
        return item -> {
            log.info("SP -> Processing item: {}", item);
            return item;
        };
    }

    @Bean
    public Job processStoredProcJob(Step processStoredProcStep) {
        return new JobBuilder("processStoredProcJob", jobRepository)
                .start(processStoredProcStep)
                .build();
    }

    @Bean
    public Step processStoredProcStep() throws IOException {
        return new StepBuilder("processStoredProcStep", jobRepository)
                .<TransactionProcedureResult, TransactionProcedureResult>chunk(10, transactionManager)
                .reader(storedProcReader())
                .processor(storedProcProcessor())
                .writer(storedProcWriter())
                .build();
    }
} 