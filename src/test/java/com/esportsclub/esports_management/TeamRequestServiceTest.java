package com.esportsclub.esports_management;

import com.esportsclub.esports_management.model.TeamRequest;
import com.esportsclub.esports_management.repository.TeamRequestRepository;
import com.esportsclub.esports_management.service.TeamRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamRequestServiceTest {

    @Mock
    private TeamRequestRepository teamRequestRepository;

    @InjectMocks
    private TeamRequestService teamRequestService;

    private TeamRequest pendingRequest;

    @BeforeEach
    void setUp() {
        pendingRequest = new TeamRequest();
        pendingRequest.setId(1);
        pendingRequest.setUserId(10);
        pendingRequest.setTeamName("Phoenix Rising");
        pendingRequest.setStatus("PENDING");
        pendingRequest.setRequestedAt(LocalDateTime.now());
    }

    @Test
    void TC_TR01_hasPendingRequestReturnsTrueWhenExists() {
        when(teamRequestRepository.findByUserIdAndStatus(10, "PENDING"))
                .thenReturn(List.of(pendingRequest));

        boolean result = teamRequestService.hasPendingRequest(10);

        assertTrue(result, "Should return true when pending request exists");
    }

    @Test
    void TC_TR02_hasPendingRequestReturnsFalseWhenNone() {
        when(teamRequestRepository.findByUserIdAndStatus(10, "PENDING"))
                .thenReturn(List.of());

        boolean result = teamRequestService.hasPendingRequest(10);

        assertFalse(result, "Should return false when no pending request exists");
    }

    @Test
    void TC_TR03_saveRequestPersistsToRepository() {
        when(teamRequestRepository.save(any(TeamRequest.class)))
                .thenAnswer(i -> i.getArgument(0));

        teamRequestService.saveRequest(pendingRequest);

        verify(teamRequestRepository, times(1)).save(pendingRequest);
    }

    @Test
    void TC_TR04_getRequestsByUserReturnsCorrectList() {
        when(teamRequestRepository.findByUserId(10)).thenReturn(List.of(pendingRequest));

        List<TeamRequest> result = teamRequestService.getRequestsByUser(10);

        assertFalse(result.isEmpty(), "Should return requests for the user");
        assertEquals(10, result.get(0).getUserId());
    }

    @Test
    void TC_TR05_approvedRequestHasApprovedStatus() {
        pendingRequest.setStatus("APPROVED");
        assertEquals("APPROVED", pendingRequest.getStatus(),
                "Approved request should have APPROVED status");
    }

    @Test
    void TC_TR06_rejectedRequestHasRejectedStatus() {
        pendingRequest.setStatus("REJECTED");
        assertEquals("REJECTED", pendingRequest.getStatus(),
                "Rejected request should have REJECTED status");
    }
}