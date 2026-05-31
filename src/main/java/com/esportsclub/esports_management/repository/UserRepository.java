package com.esportsclub.esports_management.repository;

import com.esportsclub.esports_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByRole(String role);

    List<User> findByStatus(String status);

    List<User> findByTeamName(String teamName);

    List<User> findByTeamNameAndRole(String teamName, String role);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password, u.tempPassword = :tempPassword WHERE u.id = :id")
    void updatePasswordAndTempFlag(@Param("id") int id,
                                   @Param("password") String password,
                                   @Param("tempPassword") boolean tempPassword);
}