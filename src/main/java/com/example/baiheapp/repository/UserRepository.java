package com.example.baiheapp.repository;

import com.example.baiheapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByNameAndGenderAndAgeAndHospitalNumber(
            String name, String gender, Integer age, String hospitalNumber);
}