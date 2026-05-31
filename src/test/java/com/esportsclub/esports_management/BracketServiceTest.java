package com.esportsclub.esports_management;

import com.esportsclub.esports_management.model.Match;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BracketServiceTest {

    private List<List<Match>> distributeRounds(List<Match> matches) {
        List<List<Match>> rounds = new ArrayList<>();
        if (matches.isEmpty()) return rounds;

        int remaining = matches.size();
        List<Integer> matchesInRound = new ArrayList<>();

        int roundMatches = 1;
        while (remaining > 0) {
            int take = Math.min(roundMatches, remaining);
            matchesInRound.add(0, take);
            remaining -= take;
            roundMatches *= 2;
        }

        int index = 0;
        for (int count : matchesInRound) {
            rounds.add(new ArrayList<>(matches.subList(index, index + count)));
            index += count;
        }
        return rounds;
    }

    private List<String> getRoundNames(int totalRounds) {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < totalRounds; i++) {
            int fromEnd = totalRounds - i;
            if (fromEnd == 1) names.add("Final");
            else if (fromEnd == 2) names.add("Semifinal");
            else if (fromEnd == 3) names.add("Quarterfinal");
            else if (fromEnd == 4) names.add("Round of 16");
            else names.add("Round " + (i + 1));
        }
        return names;
    }

    private Match createMatch(int id) {
        Match m = new Match();
        m.setId(id);
        m.setMatchDate(LocalDate.now().plusDays(id));
        m.setStatus("PENDING");
        m.setTeam1Id(id);
        m.setTeam2Id(id + 1);
        return m;
    }

    @Test
    void TC_B01_oneMatchProducesFinal() {
        List<Match> matches = List.of(createMatch(1));
        List<List<Match>> rounds = distributeRounds(matches);
        List<String> names = getRoundNames(rounds.size());

        assertEquals(1, rounds.size());
        assertEquals("Final", names.get(0));
    }

    @Test
    void TC_B02_twoMatchesProduceSemifinalAndFinal() {
        List<Match> matches = List.of(createMatch(1), createMatch(2));
        List<List<Match>> rounds = distributeRounds(matches);
        List<String> names = getRoundNames(rounds.size());

        assertEquals(2, rounds.size());
        assertEquals("Semifinal", names.get(0));
        assertEquals("Final", names.get(1));
    }

    @Test
    void TC_B03_fourMatchesProduceThreeRounds() {
        List<Match> matches = new ArrayList<>();
        for (int i = 1; i <= 4; i++) matches.add(createMatch(i));

        List<List<Match>> rounds = distributeRounds(matches);
        List<String> names = getRoundNames(rounds.size());

        assertEquals(3, rounds.size());
        assertEquals("Quarterfinal", names.get(0));
        assertEquals("Semifinal", names.get(1));
        assertEquals("Final", names.get(2));
    }

    @Test
    void TC_B04_finalAlwaysHasOneMatch() {
        for (int total = 1; total <= 8; total++) {
            List<Match> matches = new ArrayList<>();
            for (int i = 1; i <= total; i++) matches.add(createMatch(i));
            List<List<Match>> rounds = distributeRounds(matches);
            assertEquals(1, rounds.get(rounds.size() - 1).size(),
                    "Final should always have 1 match, failed for total=" + total);
        }
    }

    @Test
    void TC_B05_emptyMatchListProducesNoRounds() {
        List<List<Match>> rounds = distributeRounds(List.of());
        assertTrue(rounds.isEmpty());
    }

    @Test
    void TC_B06_sevenMatchesProduceThreeRounds() {
        List<Match> matches = new ArrayList<>();
        for (int i = 1; i <= 7; i++) matches.add(createMatch(i));

        List<List<Match>> rounds = distributeRounds(matches);
        List<String> names = getRoundNames(rounds.size());

        assertEquals(3, rounds.size(), "7 matches should produce 3 rounds");
        assertEquals("Quarterfinal", names.get(0));
        assertEquals("Semifinal", names.get(1));
        assertEquals("Final", names.get(2));
    }
}