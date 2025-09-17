package com.krazytop.leagueoflegends.config;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MongoConfigTest {

    @InjectMocks
    private MongoConfig mongoConfig;

    @Mock
    private MongoDatabaseFactory mongoDatabaseFactory;
    @Mock
    private MongoMappingContext  mongoMappingContext;
    @Mock
    private MongoTemplate mongoTemplate;
    @Mock
    private MongoTransactionManager mongoTransactionManager;
    @Mock
    private MongoCollection<Document> collection;
    @Mock
    private IndexOperations indexOperations;
    @Mock
    private MongoExceptionTranslator mongoExceptionTranslator;

    @Test
    void transactionManager() {
        MongoTransactionManager result = mongoConfig.transactionManager(mongoDatabaseFactory);
        assertThat(result.getDatabaseFactory()).isEqualTo(mongoDatabaseFactory);
    }

    @Test
    void mappingMongoConverter() {
        when(mongoDatabaseFactory.getExceptionTranslator()).thenReturn(mongoExceptionTranslator);
        MappingMongoConverter converter = mongoConfig.mappingMongoConverter(mongoDatabaseFactory, mongoMappingContext);
        assertThat(converter.getMappingContext()).isEqualTo(mongoMappingContext);
    }

    @Test
    void jobRepository() throws Exception {
        JobRepository jobRepository = mongoConfig.jobRepository(mongoTemplate, mongoTransactionManager);
        assertThat(jobRepository).isNotNull();
    }

    @Test
    void jobExplorer() throws Exception {
        JobExplorer jobExplorer = mongoConfig.jobExplorer(mongoTemplate, mongoTransactionManager);
        assertThat(jobExplorer).isNotNull();
    }

    @Test
    void initializeBatchSchema_new() {
        when(mongoTemplate.collectionExists("BATCH_JOB_INSTANCE")).thenReturn(false);
        when(mongoTemplate.getCollection("BATCH_SEQUENCES")).thenReturn(collection);
        when(collection.countDocuments(new Document("_id", "BATCH_JOB_INSTANCE_SEQ"))).thenReturn(0L);
        when(collection.countDocuments(new Document("_id", "BATCH_JOB_EXECUTION_SEQ"))).thenReturn(0L);
        when(collection.countDocuments(new Document("_id", "BATCH_STEP_EXECUTION_SEQ"))).thenReturn(0L);
        when(mongoTemplate.indexOps(anyString())).thenReturn(indexOperations);

        String result = mongoConfig.initializeBatchSchema(mongoTemplate);

        verify(mongoTemplate).createCollection("BATCH_JOB_INSTANCE");
        verify(mongoTemplate).createCollection("BATCH_JOB_EXECUTION");
        verify(mongoTemplate).createCollection("BATCH_STEP_EXECUTION");
        verify(mongoTemplate).createCollection("BATCH_SEQUENCES");

        verify(collection).insertOne(new Document(Map.of("_id", "BATCH_JOB_INSTANCE_SEQ", "count", 0L)));
        verify(collection).insertOne(new Document(Map.of("_id", "BATCH_JOB_EXECUTION_SEQ", "count", 0L)));
        verify(collection).insertOne(new Document(Map.of("_id", "BATCH_STEP_EXECUTION_SEQ", "count", 0L)));

        verify(indexOperations, atLeastOnce()).createIndex(any(Index.class));

        assertThat(result).isEqualTo("BatchSchemaInitialized");
    }

    @Test
    void initializeBatchSchema_alreadyExisting() {
        when(mongoTemplate.collectionExists("BATCH_JOB_INSTANCE")).thenReturn(true);

        String result = mongoConfig.initializeBatchSchema(mongoTemplate);

        verify(mongoTemplate, never()).createCollection(anyString());
        verify(mongoTemplate, never()).getCollection(anyString());
        verify(mongoTemplate, never()).indexOps(anyString());

        assertThat(result).isEqualTo("BatchSchemaInitialized");
    }
}
