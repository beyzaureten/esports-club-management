package com.esportsclub.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.esportsclub.model.Match;

public class MatchServiceTest {

    private MatchService matchService;

    @BeforeEach
    void setUp() {
        matchService = new MatchService();
    }

    @Test
    void testCreateMatchWithInvalidTournamentId() {
        Match match = new Match(0, 0, 1, 2, 0, 0, 0, "2026-04-01", "SCHEDULED");
        boolean result = matchService.createMatch(match);
        assertFalse(result, "Should fail with invalid tournament ID");
    }

    @Test
    void testCreateMatchWithSameTeams() {
        Match match = new Match(0, 1, 2, 2, 0, 0, 0, "2026-04-01", "SCHEDULED");
        boolean result = matchService.createMatch(match);
        assertFalse(result, "Should fail when both teams are the same");
    }

    @Test
    void testCreateMatchWithEmptyDate() {
        Match match = new Match(0, 1, 1, 2, 0, 0, 0, "", "SCHEDULED");
        boolean result = matchService.createMatch(match);
        assertFalse(result, "Should fail with empty date");
    }

    @Test
    void testCreateValidMatch() {
        Match match = new Match(0, 1, 1, 2, 0, 0, 0, "2026-04-01", "SCHEDULED");
        boolean result = matchService.createMatch(match);
        assertTrue(result, "Should succeed with valid match");
    }

    @Test
    void testEnterResultWithNegativeScores() {
        boolean result = matchService.enterResult(1, 1, -1, 2);
        assertFalse(result, "Should fail with negative scores");
    }

    @Test
    void testEnterResultWithInvalidWinner() {
        boolean result = matchService.enterResult(1, 99, 2, 1);
        assertFalse(result, "Should fail with invalid winner ID");
    }

    @Test
    void testIsMatchFinishedWithInvalidId() {
        boolean result = matchService.isMatchFinished(99999);
        assertFalse(result, "Should return false for non existing match");
    }

    @Test
    void testGetAllMatches() {
        var result = matchService.getAllMatches();
        assertNotNull(result, "Should return a list, not null");
    }
}