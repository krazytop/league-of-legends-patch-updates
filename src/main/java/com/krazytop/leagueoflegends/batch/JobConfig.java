package com.krazytop.leagueoflegends.batch;

import com.krazytop.leagueoflegends.batch.model.PatchMetadata;
import com.krazytop.leagueoflegends.batch.step.FilterPatchesVersionTasklet;
import com.krazytop.leagueoflegends.batch.step.PatchItemProcessor;
import com.krazytop.leagueoflegends.batch.step.PatchItemReader;
import com.krazytop.leagueoflegends.batch.step.PatchItemWriter;
import com.krazytop.leagueoflegends.entity.Patch;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;

import static com.krazytop.leagueoflegends.service.PatchService.SUPPORTED_LANGUAGES;

@AllArgsConstructor
@Configuration
public class JobConfig {

    private static final int CHUNK_SIZE = SUPPORTED_LANGUAGES.size();

    @Bean
    public Step updatePatchesStep(JobRepository jobRepository, MongoTransactionManager mongoTransactionManager, PatchItemReader patchItemReader, PatchItemProcessor patchItemProcessor, PatchItemWriter patchItemWriter) {
        return new StepBuilder("updatePatchesStep", jobRepository)
                .<PatchMetadata, Patch>chunk(CHUNK_SIZE, mongoTransactionManager)
                .reader(patchItemReader)
                .processor(patchItemProcessor)
                .writer(patchItemWriter)
                .build();
    }

    @Bean
    public TaskletStep filterPatchesVersionTaskletStep(JobRepository jobRepository, MongoTransactionManager mongoTransactionManager, FilterPatchesVersionTasklet filterPatchesVersionTasklet) {
        return new StepBuilder("filterPatchesVersionTasklet", jobRepository)
                .tasklet(filterPatchesVersionTasklet, mongoTransactionManager)
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, PatchJobListener patchJobListener, TaskletStep filterPatchesVersionTaskletStep, Step updatePatchesStep) {
        return new JobBuilder("checkAndUpdatePatchesJob", jobRepository)
                .listener(patchJobListener)
                .start(filterPatchesVersionTaskletStep)
                .next(updatePatchesStep)
                .build();
    }

}