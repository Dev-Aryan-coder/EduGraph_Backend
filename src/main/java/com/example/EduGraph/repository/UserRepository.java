package com.example.EduGraph.repository;

import com.example.EduGraph.entity.User;
import com.example.EduGraph.enums.AccountStatus;
import com.example.EduGraph.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndStatus(String email, AccountStatus status);

    boolean existsByEmail(String email);

    List<User> findByClassroomId(Long classroomId);

    List<User> findByClassroomIdAndRole(Long classroomId, UserRole role);

    Page<User> findByCollegeIdAndRole(Long collegeId, UserRole role, Pageable pageable);

    List<User> findByCollegeIdAndRole(Long collegeId, UserRole role);

    long countByCollegeIdAndRole(Long collegeId, UserRole role);

    List<User> findByRole(UserRole role);

    long countByRole(UserRole role);
}