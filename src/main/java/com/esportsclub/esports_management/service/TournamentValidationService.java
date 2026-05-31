package com.esportsclub.esports_management.service;

import com.esportsclub.esports_management.model.Tournament;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TournamentValidationService {

    public String validate(Tournament tournament) {

        if (tournament.getStartDate() == null || tournament.getEndDate() == null) {
            return "Start date and end date cannot be empty.";
        }

        if (tournament.getEndDate().isBefore(tournament.getStartDate())) {
            return "End date cannot be before start date.";
        }

        if (tournament.getEndDate().isEqual(tournament.getStartDate())) {
            return "End date cannot be the same as start date.";
        }

        LocalDate today = LocalDate.now();
        boolean startInPast = !tournament.getStartDate().isAfter(today);
        boolean endInPast = tournament.getEndDate().isBefore(today);
        boolean startInFuture = tournament.getStartDate().isAfter(today);
        boolean endInFuture = !tournament.getEndDate().isBefore(today);
        String status = tournament.getStatus();

        // Her iki tarih de geçmiş → sadece FINISHED olabilir
        if (startInPast && endInPast) {
            if (!"FINISHED".equalsIgnoreCase(status)) {
                return "Tournament dates are fully in the past. Status must be Finished.";
            }
        }

        // Başlangıç geçti, bitiş gelecekte → sadece ONGOING olabilir
        if (startInPast && endInFuture && !endInPast) {
            if (!"ONGOING".equalsIgnoreCase(status)) {
                return "Tournament is currently in progress. Status must be Ongoing.";
            }
        }

        // Her iki tarih de gelecekte → sadece UPCOMING olabilir
        if (startInFuture) {
            if (!"UPCOMING".equalsIgnoreCase(status)) {
                return "Tournament has not started yet. Status must be Upcoming.";
            }
        }

        return null;
    }
}