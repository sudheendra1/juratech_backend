package com.juratech.backend.model;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Data
@Document(collection = "loan_documents")
public class LoanDocumentModel {
        @Id
        private String id;


        private String userId;
        private String submittedByUid;
        private String submittedBy;
        private String name;
        private String status;

        private String reviewer;
        private String modifiedBy;
        private Date submittedAt;
        private String lastModified;
        private String reviewedAt;


        private Map<String, Object> borrowerDetails;
        private Map<String, Object> guarantors;
        private Map<String, Object> loanFacilities;
        private Map<String, Object> registrationOfSecurity;
        private Map<String, Object> sanctionLetter;
        private Map<String, Object> securities;
        private Map<String, Object> otherDocuments;
    }

