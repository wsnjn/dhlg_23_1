package com.example.baiheapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "ad8_responses")
public class Ad8Response {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hospital_id")
    private String hospitalId;

    @Column(name = "patient_name")
    private String patientName;

    @Column(name = "response_id")
    private String responseId;

    @Column(name = "question_id")
    private Integer questionId;

    @Column(name = "selected_option")
    private Integer selectedOption;

    @Column(name = "created_at")
    private Date createdAt;
}