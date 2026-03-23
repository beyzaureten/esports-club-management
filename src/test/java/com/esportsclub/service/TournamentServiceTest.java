package com.esportsclub.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.esportsclub.model.Tournament;

public class TournamentServiceTest {

    private TournamentService tournamentService;

    @BeforeEach
    void setUp() {
        tournamentService = new TournamentService();
    }

    @Test
    void testCreateTournamentWithEmptyName() {
        Tournament tournament = new Tournament(0, "", 1, 8, "2026-04-01", "2026-04-30", "UPCOMING");
        boolean result = tournamentService.createTournament(tournament);
        assertFalse(result, "Should fail with empty name");
    }

    @Test
    void testCreateTournamentWithInvalidMaxTeams() {
        Tournament tournament = new Tournament(0, "Spring Cup", 1, 0, "2026-04-01", "2026-04-30", "UPCOMING");
        boolean result = tournamentService.createTournament(tournament);
        assertFalse(result, "Should fail with 0 max teams");
    }

    @Test
    void testCreateTournamentWithNullDates() {
        Tournament tournament = new Tournament(0, "Spring Cup", 1, 8, null, null, "UPCOMING");
        boolean result = tournamentService.createTournament(tournament);
        assertFalse(result, "Should fail with null dates");
    }

    @Test
    void testCreateValidTournament() {
        Tournament tournament = new Tournament(0, "Spring Cup", 1, 8, "2026-04-01", "2026-04-30", "UPCOMING");
        boolean result = tournamentService.createTournament(tournament);
        assertTrue(result, "Should succeed with valid tournament");
    }

    @Test
    void testUpdateTournamentWithEmptyName() {
        Tournament tournament = new Tournament(1, "", 1, 8, "2026-04-01", "2026-04-30", "UPCOMING");
        boolean result = tournamentService.updateTournament(tournament);
        assertFalse(result, "Should fail with empty name");
    }

    @Test
    void testGetTournamentsByStatus() {
        var result = tournamentService.getTournamentsByStatus("UPCOMING");
        assertNotNull(result, "Should return a list, not null");
    }

    @Test
    void testGetAllTournaments() {
        var result = tournamentService.getAllTournaments();
        assertNotNull(result, "Should return a list, not null");
    }
}