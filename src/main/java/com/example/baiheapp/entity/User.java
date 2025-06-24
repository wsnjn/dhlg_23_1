package com.example.baiheapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String gender;
    private Integer age;

    @Column(name = "hospital_number")
    private String hospitalNumber;

    @Column(name = "assessment_date")
    private Date assessmentDate;
}