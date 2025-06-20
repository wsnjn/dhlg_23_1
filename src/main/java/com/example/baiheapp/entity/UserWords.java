package com.example.baiheapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "user_words")
public class UserWords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "word_list")
    private String wordList;

    @Column(name = "created_at")
    private Date createdAt;
}