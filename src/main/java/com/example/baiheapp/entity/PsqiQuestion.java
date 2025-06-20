package com.example.baiheapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "psqi_questions")
public class PsqiQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "question_text")
    private String text;

    private String options;

    @Column(name = "question_type")
    private String type;

    @Column(name = "parent_id")
    private Integer parentId;
}