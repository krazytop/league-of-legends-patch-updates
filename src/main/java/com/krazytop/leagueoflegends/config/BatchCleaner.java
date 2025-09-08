package com.krazytop.leagueoflegends.config;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class BatchCleaner {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public BatchCleaner(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PreDestroy
    public void cleanupBatchCollections() {
        String[] collectionsToDrop = {
                "BATCH_JOB_INSTANCE",
                "BATCH_JOB_EXECUTION",
                "BATCH_STEP_EXECUTION",
                "BATCH_SEQUENCES"
        };
        for (String collectionName : collectionsToDrop) {
            if (mongoTemplate.collectionExists(collectionName)) {
                mongoTemplate.dropCollection(collectionName);
            }
        }
    }
}
