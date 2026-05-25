package com.juratech.backend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

@Configuration
public class MongoConverterConfig {

    @Autowired
    private MappingMongoConverter mappingMongoConverter;

    @PostConstruct
    public void setUpMongoEscapeCharacterConversion() {
        // This safely runs AFTER MongoConfig has finished building everything
        mappingMongoConverter.setMapKeyDotReplacement("_dot_");
    }
}