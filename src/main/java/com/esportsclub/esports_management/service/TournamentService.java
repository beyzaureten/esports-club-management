package com.esportsclub.esports_management.service;

import com.esportsclub.esports_management.model.Tournament;
import com.esportsclub.esports_management.repository.TournamentRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentValidationService validationService;

    public TournamentService(TournamentRepository tournamentRepository,
                             TournamentValidationService validationService) {
        this.tournamentRepository = tournamentRepository;
        this.validationService = validationService;
    }

    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    public Optional<Tournament> getById(int id) {
        return tournamentRepository.findById(id);
    }

    public String saveTournament(Tournament tournament) {
        String error = validationService.validate(tournament);
        if (error != null) return error;
        tournamentRepository.save(tournament);
        return null;
    }

    public void deleteTournament(int id) {
        tournamentRepository.deleteById(id);
    }

    public List<Tournament> getByStatus(String status) {
        return tournamentRepository.findByStatus(status);
    }
}
