package com.juratech.backend.model;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;


@Data
@Document(collection = "activities")
public class ActivityModel {

        @Id
        private String id;
        private String type;
        private String description;
        private String userId;
        private String userName;

        private LocalDateTime createdAt;

}
