package com.example.baiheapp.repository;

import com.example.baiheapp.entity.UserWords;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserWordsRepository extends JpaRepository<UserWords, Long> {
    List<UserWords> findByUserIdOrderByCreatedAtDesc(String userId);
}