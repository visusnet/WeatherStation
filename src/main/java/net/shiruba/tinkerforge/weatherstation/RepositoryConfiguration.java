package net.shiruba.tinkerforge.weatherstation;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories
public class RepositoryConfiguration extends AbstractMongoConfiguration {

    @Bean
    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient();
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), getDatabaseName());
    }

    @Override
    protected String getDatabaseName() {
        return "weatherstation";
    }

    @Override
    protected String getMappingBasePackage() {
        return "net.shiruba.tinkerforge.weatherstation.data";
    }
}
