package com.juratech.backend.config;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration{

        @Override
        protected String getDatabaseName() {
            return "juratech";
        }

        @Override
        public MongoClient mongoClient() {
            // Hardwiring your exact Atlas URI here
            ConnectionString connectionString = new ConnectionString(
                    "mongodb+srv://sudheendra1:VLIG2pzgUgMjTZQZ@juratech.mfkpjyp.mongodb.net/juratech?appName=Juratech"
            );

            MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .build();

            return MongoClients.create(mongoClientSettings);
        }
    }


