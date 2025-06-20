package com.example.baiheapp.repository;

import com.example.baiheapp.entity.Ad8Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface Ad8QuestionRepository extends JpaRepository<Ad8Question, Integer> {
    List<Ad8Question> findAllByOrderByIdAsc();
}