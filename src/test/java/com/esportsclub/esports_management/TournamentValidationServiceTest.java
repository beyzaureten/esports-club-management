package com.esportsclub.esports_management;

import com.esportsclub.esports_management.model.Tournament;
import com.esportsclub.esports_management.service.TournamentValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class TournamentValidationServiceTest {

    private TournamentValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new TournamentValidationService();
    }

    private Tournament buildTournament(LocalDate start, LocalDate end, String status) {
        Tournament t = new Tournament();
        t.setStartDate(start);
        t.setEndDate(end);
        t.setStatus(status);
        t.setName("Test Tournament");
        t.setMaxTeams(8);
        return t;
    }

    @Test
    void TC_T01_nullDatesReturnsError() {
        Tournament t = buildTournament(null, null, "UPCOMING");
        String result = validationService.validate(t);
        assertNotNull(result, "Null dates should return error");
        assertTrue(result.toLowerCase().contains("date") || result.toLowerCase().contains("empty"),
                "Error should mention dates");
    }

    @Test
    void TC_T02_endBeforeStartReturnsError() {
        LocalDate start = LocalDate.now().plusDays(10);
        LocalDate end = LocalDate.now().plusDays(5);
        Tournament t = buildTournament(start, end, "UPCOMING");
        String result = validationService.validate(t);
        assertNotNull(result, "End before start should return error");
    }

    @Test
    void TC_T03_sameDatesReturnsError() {
        LocalDate date = LocalDate.now().plusDays(10);
        Tournament t = buildTournament(date, date, "UPCOMING");
        String result = validationService.validate(t);
        assertNotNull(result, "Same start and end date should return error");
    }

    @Test
    void TC_T04_pastEndDateWithUpcomingStatusReturnsError() {
        LocalDate start = LocalDate.now().minusDays(20);
        LocalDate end = LocalDate.now().minusDays(5);
        Tournament t = buildTournament(start, end, "UPCOMING");
        String result = validationService.validate(t);
        assertNotNull(result, "Past end date with UPCOMING status should return error");
    }

    @Test
    void TC_T05_pastEndDateWithOngoingStatusReturnsError() {
        LocalDate start = LocalDate.now().minusDays(20);
        LocalDate end = LocalDate.now().minusDays(5);
        Tournament t = buildTournament(start, end, "ONGOING");
        String result = validationService.validate(t);
        assertNotNull(result, "Past end date with ONGOING status should return error");
    }

    @Test
    void TC_T06_futureStartWithFinishedStatusReturnsError() {
        LocalDate start = LocalDate.now().plusDays(10);
        LocalDate end = LocalDate.now().plusDays(20);
        Tournament t = buildTournament(start, end, "FINISHED");
        String result = validationService.validate(t);
        assertNotNull(result, "Future start date with FINISHED status should return error");
    }

    @Test
    void TC_T07_validTournamentReturnsNull() {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now().plusDays(10);
        Tournament t = buildTournament(start, end, "ONGOING");
        String result = validationService.validate(t);
        assertNull(result, "Valid tournament data should return null (no error)");
    }
}