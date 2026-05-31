package com.esportsclub.esports_management.repository;

import com.esportsclub.esports_management.model.TeamRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeamRequestRepository extends JpaRepository<TeamRequest, Integer> {
    List<TeamRequest> findByStatus(String status);
    List<TeamRequest> findByUserId(int userId);
    List<TeamRequest> findByUserIdAndStatus(int userId, String status);
}