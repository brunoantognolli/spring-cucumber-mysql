package com.example.springcucumber.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StreamUtils;

import com.example.springcucumber.entity.TransactionProcedureResult;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AnonymousBlockBatchConfig {

    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private String loadSqlFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("anonymousBlock.sql");
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    @Bean
    public JdbcCursorItemReader<TransactionProcedureResult> anonymousBlockReader() throws IOException {
        // First, execute the SQL script to create temporary tables
        String fullSql = loadSqlFile();
        
        var paramMap = new java.util.HashMap<String, Object>();
        paramMap.put("pendingMultiplier", 1.1);
        paramMap.put("failedMultiplier", 1.15);

        namedParameterJdbcTemplate.update(fullSql, paramMap);

        return new JdbcCursorItemReaderBuilder<TransactionProcedureResult>()
                .name("anonymousBlockReader")
                .dataSource(dataSource)
                .sql("SELECT * FROM tmpTransaction2")
                .rowMapper(new BeanPropertyRowMapper<>(TransactionProcedureResult.class))
                .build();
    }

    @Bean
    public JpaItemWriter<TransactionProcedureResult> anonymousBlockWriter() {
        return new JpaItemWriterBuilder<TransactionProcedureResult>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public ItemProcessor<TransactionProcedureResult, TransactionProcedureResult> anonymousBlockProcessor() {
        return item -> {
            log.info("AnonymousBlock -> Processing item: {}", item);
            return item;
        };
    }

    @Bean
    public Job processAnonymousBlockJob(Step processAnonymousBlockStep) {
        return new JobBuilder("processAnonymousBlockJob", jobRepository)
                .start(processAnonymousBlockStep)
                .build();
    }

    @Bean
    public Step processAnonymousBlockStep() throws IOException {
        return new StepBuilder("processAnonymousBlockStep", jobRepository)
                .<TransactionProcedureResult, TransactionProcedureResult>chunk(10, transactionManager)
                .reader(anonymousBlockReader())
                .processor(anonymousBlockProcessor())
                .writer(anonymousBlockWriter())
                .build();
    }
} 