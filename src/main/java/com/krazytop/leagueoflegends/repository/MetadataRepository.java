package com.krazytop.leagueoflegends.repository;

import com.krazytop.leagueoflegends.entity.Metadata;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface MetadataRepository extends MongoRepository<Metadata, String> {

    @Query("{ '_id': 1 }")
    Optional<Metadata> findMetadata();
}
