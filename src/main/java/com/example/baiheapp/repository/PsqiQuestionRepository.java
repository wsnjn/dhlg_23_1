package com.example.baiheapp.repository;

import com.example.baiheapp.entity.PsqiQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PsqiQuestionRepository extends JpaRepository<PsqiQuestion, Integer> {
    List<PsqiQuestion> findByParentIdIsNullOrderByIdAsc();
    List<PsqiQuestion> findByParentIdOrderByIdAsc(Integer parentId);
}