package com.krazytop.leagueoflegends;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@AllArgsConstructor
@ConfigurationPropertiesScan
@EnableBatchProcessing
@EnableMongoRepositories(basePackages = {"com.krazytop.leagueoflegends.repository"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class PatchUpdatesApplication {

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(PatchUpdatesApplication.class, args)));
    }
}
