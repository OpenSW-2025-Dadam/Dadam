package com.example.dadambackend.domain.user.repository;

import com.example.dadambackend.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 로그인/회원가입용
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);   // ← 반드시 필요!!

    // 가족 기능용
    Optional<User> findByFamilyCode(String familyCode);

    boolean existsByFamilyCode(String familyCode);

    List<User> findAllByFamilyCode(String familyCode);
}
