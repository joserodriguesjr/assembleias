package com.teste.assembleia.service;

import com.teste.assembleia.model.Agenda;
import com.teste.assembleia.repository.AgendaRepository;
import com.teste.assembleia.repository.VotingSessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final VotingSessionRepository votingSessionRepository;

    public Agenda create(Agenda agenda) {
        return agendaRepository.save(agenda);
    }

    public Optional<Agenda> findById(Long agendaId) {
        return agendaRepository.findById(agendaId);
    }

}
