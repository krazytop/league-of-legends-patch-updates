package com.krazytop.leagueoflegends.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatchCleanerTest {

    @InjectMocks
    private BatchCleaner batchCleaner;

    @Mock
    private MongoTemplate mongoTemplate;

    @Test
    void cleanupBatchCollections_existingCollections() {
        when(mongoTemplate.collectionExists(anyString())).thenReturn(true);

        batchCleaner.cleanupBatchCollections();

        verify(mongoTemplate).collectionExists("BATCH_JOB_INSTANCE");
        verify(mongoTemplate).collectionExists("BATCH_JOB_EXECUTION");
        verify(mongoTemplate).collectionExists("BATCH_STEP_EXECUTION");
        verify(mongoTemplate).collectionExists("BATCH_SEQUENCES");

        verify(mongoTemplate).dropCollection("BATCH_JOB_INSTANCE");
        verify(mongoTemplate).dropCollection("BATCH_JOB_EXECUTION");
        verify(mongoTemplate).dropCollection("BATCH_STEP_EXECUTION");
        verify(mongoTemplate).dropCollection("BATCH_SEQUENCES");
    }

    @Test
    void cleanupBatchCollections_MissingCollections() {
        when(mongoTemplate.collectionExists(anyString())).thenReturn(false);

        batchCleaner.cleanupBatchCollections();

        verify(mongoTemplate, never()).dropCollection(anyString());
    }

}
