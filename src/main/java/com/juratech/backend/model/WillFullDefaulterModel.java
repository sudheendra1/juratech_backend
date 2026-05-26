package com.juratech.backend.model;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "willful_defaulters")
public class WillFullDefaulterModel {

        @Id
        private String id;

        // 1. Branch Details
        private String branchName;
        private String branchId;
        private String region;
        private String zone;

        // 2. Core Entities (Using Maps for maximum flexibility with dynamic fields)
        private List<Map<String, Object>> borrowers;
        private List<Map<String, Object>> facilities;
        private List<Map<String, Object>> guarantors;

        // 3. Loan Details
        private Map<String, Object> originalSanction;
        private Map<String, Object> lastRenewal;
        private String npaDate;
        private Map<String, Object> outstanding;

        // 4. Complex Analysis Sections
        private Map<String, Object> groundsForWillfulDefaulter;
        private List<Map<String, Object>> diversionDetails;
        private Map<String, Object> siphoning;
        private Map<String, Object> disposalOfAssets;
        private Map<String, Object> failureToInfuse;

        // 5. System Metadata
        private String submittedBy;
        private String submittedByUid;
        private Date submittedAt;
        private String status;
        private String module;


}

