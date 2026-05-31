package com.esportsclub.esports_management.service;

import com.esportsclub.esports_management.model.TeamRequest;
import com.esportsclub.esports_management.repository.TeamRequestRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TeamRequestService {

    private final TeamRequestRepository teamRequestRepository;

    public TeamRequestService(TeamRequestRepository teamRequestRepository) {
        this.teamRequestRepository = teamRequestRepository;
    }

    public List<TeamRequest> getAllRequests() {
        return teamRequestRepository.findAll();
    }

    public List<TeamRequest> getPendingRequests() {
        return teamRequestRepository.findByStatus("PENDING");
    }

    public List<TeamRequest> getRequestsByUser(int userId) {
        return teamRequestRepository.findByUserId(userId);
    }

    public boolean hasPendingRequest(int userId) {
        return !teamRequestRepository.findByUserIdAndStatus(userId, "PENDING").isEmpty();
    }

    public void saveRequest(TeamRequest request) {
        teamRequestRepository.save(request);
    }

    public TeamRequest getById(int id) {
        return teamRequestRepository.findById(id).orElse(null);
    }
}