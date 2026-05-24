package com.juratech.backend.model;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Map;

@Data
@Document(collection = "reviews")
public class ReviewModel {

    @Id
    private String id; // MongoDB will auto-generate this, similar to Firestore's auto-ID

    private String submissionId;
    private String reviewerUid;
    private String reviewerName;

    // This perfectly handles your dynamic S3 URLs as keys and "all good"/"change" as values
    private Map<String, String> documentComments;

    // Storing as Strings to perfectly match the "July 1, 2025..." format coming from your frontend
    private String originalSubmittedAt;
    private String submittedAt;
    private String lastUpdated;


}
