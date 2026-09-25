package com.example.EduGraph.repository;

import com.example.EduGraph.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollegeRepository extends JpaRepository<College, Long> {

    boolean existsByName(String name);

    Optional<College> findByName(String name);
}
