package com.example.baiheapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ad8_questions")
public class Ad8Question {
    @Id
    private Integer id;

    @Column(name = "question_text")
    private String text;

    private String option1;
    private String option2;
    private String option3;

    @Column(name = "correct_option")
    private Integer correct;
}