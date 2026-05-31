package com.esportsclub.esports_management;

import com.esportsclub.esports_management.model.Match;
import com.esportsclub.esports_management.model.Tournament;
import com.esportsclub.esports_management.repository.MatchRepository;
import com.esportsclub.esports_management.repository.TournamentRepository;
import com.esportsclub.esports_management.service.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private MatchService matchService;

    private Tournament validTournament;

    @BeforeEach
    void setUp() {
        validTournament = new Tournament();
        validTournament.setId(1);
        validTournament.setName("Test Tournament");
        validTournament.setStartDate(LocalDate.now().minusDays(10));
        validTournament.setEndDate(LocalDate.now().plusDays(30));
        validTournament.setStatus("ONGOING");
    }

    private Match buildMatch(Integer tournamentId, Integer team1Id, Integer team2Id, LocalDate date) {
        Match m = new Match();
        m.setTournamentId(tournamentId);
        m.setTeam1Id(team1Id);
        m.setTeam2Id(team2Id);
        m.setMatchDate(date);
        m.setStatus("PENDING");
        m.setTeam1Score(0);
        m.setTeam2Score(0);
        return m;
    }

    @Test
    void TC_M01_nullTournamentReturnsError() {
        Match m = buildMatch(null, 1, 2, LocalDate.now().plusDays(5));

        String result = matchService.saveMatchWithValidation(m);

        assertNotNull(result, "Null tournamentId should return error");
        assertTrue(result.toLowerCase().contains("tournament"));
        verify(tournamentRepository, never()).findById(anyInt());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void TC_M02_sameTeamsReturnsError() {
        Match m = buildMatch(1, 1, 1, LocalDate.now().plusDays(5));

        String result = matchService.saveMatchWithValidation(m);

        assertNotNull(result, "Same team1 and team2 should return error");
        verify(tournamentRepository, never()).findById(anyInt());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void TC_M03_nullDateReturnsError() {
        Match m = buildMatch(1, 1, 2, null);

        String result = matchService.saveMatchWithValidation(m);

        assertNotNull(result, "Null date should return error");
        verify(tournamentRepository, never()).findById(anyInt());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void TC_M04_dateBeforeTournamentStartReturnsError() {
        when(tournamentRepository.findById(1)).thenReturn(Optional.of(validTournament));

        Match m = buildMatch(1, 1, 2, LocalDate.now().minusDays(15));

        String result = matchService.saveMatchWithValidation(m);

        assertNotNull(result, "Date before tournament start should return error");
        verify(tournamentRepository, times(1)).findById(1);
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void TC_M05_dateAfterTournamentEndReturnsError() {
        when(tournamentRepository.findById(1)).thenReturn(Optional.of(validTournament));

        Match m = buildMatch(1, 1, 2, LocalDate.now().plusDays(60));

        String result = matchService.saveMatchWithValidation(m);

        assertNotNull(result, "Date after tournament end should return error");
        verify(tournamentRepository, times(1)).findById(1);
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void TC_M06_validMatchSavesSuccessfully() {
        when(tournamentRepository.findById(1)).thenReturn(Optional.of(validTournament));
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> i.getArgument(0));

        Match m = buildMatch(1, 1, 2, LocalDate.now().plusDays(5));

        String result = matchService.saveMatchWithValidation(m);

        assertNull(result, "Valid match should return null (no error)");
        verify(tournamentRepository, times(1)).findById(1);
        verify(matchRepository, times(1)).save(any(Match.class));
    }

    @Test
    void TC_M07_pastDatedMatchAutoFinishes() {
        Match m = buildMatch(1, 1, 2, LocalDate.now().minusDays(3));
        m.setId(1);

        when(matchRepository.findAll()).thenReturn(List.of(m));
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> i.getArgument(0));

        matchService.updatePastMatchesAutomatically();

        assertEquals("FINISHED", m.getStatus(), "Past match should be auto-set to FINISHED");
        verify(matchRepository, times(1)).findAll();
        verify(matchRepository, times(1)).save(any(Match.class));
    }

    @Test
    void TC_M08_getAllMatchesReturnsNonEmpty() {
        Match m = buildMatch(1, 1, 2, LocalDate.now().plusDays(5));

        when(matchRepository.findAll()).thenReturn(List.of(m));

        List<Match> result = matchService.getAllMatches();

        assertNotNull(result);
        assertFalse(result.isEmpty(), "getAllMatches should return non-empty list");
        verify(matchRepository, times(1)).findAll();
    }
}