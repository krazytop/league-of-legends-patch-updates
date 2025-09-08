package com.krazytop.leagueoflegends;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Date;

@ConfigurationPropertiesScan
@EnableBatchProcessing
@EnableMongoRepositories(basePackages = {"com.krazytop.leagueoflegends.repository"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class PatchUpdatesApplication {

    private final JobLauncher jobLauncher;
    private final Job job;

    public PatchUpdatesApplication(JobLauncher jobLauncher, Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    public static void main(String[] args) {
        SpringApplication.run(PatchUpdatesApplication.class, args);
    }

    @Bean
    public CommandLineRunner runBatch(ApplicationContext context) {
        return args -> {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addDate("runTime", new Date())
                    .addLong("jobId", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(job, jobParameters);
            SpringApplication.exit(context, () -> 0);
        };
    }
}
