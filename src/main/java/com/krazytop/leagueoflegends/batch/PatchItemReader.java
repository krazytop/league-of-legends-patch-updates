package com.krazytop.leagueoflegends.batch;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PatchItemReader implements ItemReader<PatchMetadata> {

    private List<PatchMetadata> newPatchesMetadata;
    private int currentIndex = 0;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
        this.newPatchesMetadata = (List<PatchMetadata>) jobContext.get("newPatchesMetadata");
    }

    @Override
    public PatchMetadata read() {
        if (currentIndex < newPatchesMetadata.size()) {
            return newPatchesMetadata.get(currentIndex++);
        } else {
            return null;
        }
    }
}
