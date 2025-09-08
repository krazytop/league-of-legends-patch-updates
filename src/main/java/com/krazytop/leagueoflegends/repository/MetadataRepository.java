package com.krazytop.leagueoflegends.repository;

import com.krazytop.leagueoflegends.entity.Metadata;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MetadataRepository extends MongoRepository<Metadata, String> {

}
