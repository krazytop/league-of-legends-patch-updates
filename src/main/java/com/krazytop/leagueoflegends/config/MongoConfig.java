package com.krazytop.leagueoflegends.config;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.MongoJobExplorerFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MongoJobRepositoryFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.Map;

@Configuration
public class MongoConfig {

    @Bean
    public MongoTemplate mongoTemplate(
            MongoDatabaseFactory mongoDatabaseFactory,
            MappingMongoConverter mappingMongoConverter) {
        return new MongoTemplate(mongoDatabaseFactory, mappingMongoConverter);
    }

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTransactionManager(mongoDatabaseFactory);
    }

    @Bean
    public MongoMappingContext mongoMappingContext() {
        return new MongoMappingContext();
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter(
            MongoDatabaseFactory mongoDatabaseFactory,
            MongoMappingContext mongoMappingContext) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDatabaseFactory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
        converter.setMapKeyDotReplacement(".");
        converter.afterPropertiesSet();
        return converter;
    }

    @Bean
    public JobRepository jobRepository(MongoTemplate mongoTemplate, MongoTransactionManager mongoTransactionManager) throws Exception {
        MongoJobRepositoryFactoryBean factory = new MongoJobRepositoryFactoryBean();
        factory.setMongoOperations(mongoTemplate);
        factory.setTransactionManager(mongoTransactionManager);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean
    public JobExplorer jobExplorer(MongoTemplate mongoTemplate, MongoTransactionManager transactionManager) throws Exception {
        var mongoExplorerFactoryBean = new MongoJobExplorerFactoryBean();
        mongoExplorerFactoryBean.setMongoOperations(mongoTemplate);
        mongoExplorerFactoryBean.setTransactionManager(transactionManager);
        mongoExplorerFactoryBean.afterPropertiesSet();
        return mongoExplorerFactoryBean.getObject();
    }

    @Bean
    public String initializeBatchSchema(MongoTemplate mongoTemplate) {
        if (!mongoTemplate.collectionExists("BATCH_JOB_INSTANCE")) {
            mongoTemplate.createCollection("BATCH_JOB_INSTANCE");
            mongoTemplate.createCollection("BATCH_JOB_EXECUTION");
            mongoTemplate.createCollection("BATCH_STEP_EXECUTION");
            mongoTemplate.createCollection("BATCH_SEQUENCES");
            MongoCollection<Document> batchSequences = mongoTemplate.getCollection("BATCH_SEQUENCES");
            if (batchSequences.countDocuments(new Document("_id", "BATCH_JOB_INSTANCE_SEQ")) == 0) {
                batchSequences.insertOne(new Document(Map.of("_id", "BATCH_JOB_INSTANCE_SEQ", "count", 0L)));
            }
            if (batchSequences.countDocuments(new Document("_id", "BATCH_JOB_EXECUTION_SEQ")) == 0) {
                batchSequences.insertOne(new Document(Map.of("_id", "BATCH_JOB_EXECUTION_SEQ", "count", 0L)));
            }
            if (batchSequences.countDocuments(new Document("_id", "BATCH_STEP_EXECUTION_SEQ")) == 0) {
                batchSequences.insertOne(new Document(Map.of("_id", "BATCH_STEP_EXECUTION_SEQ", "count", 0L)));
            }
            mongoTemplate.indexOps("BATCH_JOB_INSTANCE")
                    .createIndex(new Index().on("jobName", Sort.Direction.ASC).named("job_name_idx"));
            mongoTemplate.indexOps("BATCH_JOB_INSTANCE")
                    .createIndex(new Index().on("jobName", Sort.Direction.ASC)
                            .on("jobKey", Sort.Direction.ASC)
                            .named("job_name_key_idx"));
            mongoTemplate.indexOps("BATCH_JOB_INSTANCE")
                    .createIndex(new Index().on("jobInstanceId", Sort.Direction.DESC).named("job_instance_idx"));
            mongoTemplate.indexOps("BATCH_JOB_EXECUTION")
                    .createIndex(new Index().on("jobInstanceId", Sort.Direction.ASC).named("job_instance_idx"));
            mongoTemplate.indexOps("BATCH_JOB_EXECUTION")
                    .createIndex(new Index().on("jobInstanceId", Sort.Direction.ASC)
                            .on("status", Sort.Direction.ASC)
                            .named("job_instance_status_idx"));
            mongoTemplate.indexOps("BATCH_STEP_EXECUTION")
                    .createIndex(new Index().on("stepExecutionId", Sort.Direction.ASC).named("step_execution_idx"));
        }
        return "BatchSchemaInitialized";
    }
}