package com.teste.assembleia.service;

import com.teste.assembleia.dto.CreateVotingSessionDTO;
import com.teste.assembleia.exception.NotFoundException;
import com.teste.assembleia.model.Agenda;
import com.teste.assembleia.model.VotingSession;
import com.teste.assembleia.repository.AgendaRepository;
import com.teste.assembleia.repository.VotingSessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class VotingSessionService {

    private final AgendaRepository agendaRepository;
    private final VotingSessionRepository votingSessionRepository;

    public VotingSession create(Long agendaId, CreateVotingSessionDTO dto) {
        Agenda agenda = agendaRepository.findById(agendaId)
                .orElseThrow(() -> new NotFoundException("Pauta não encontrada"));

//        validateSessionTimes(dto.getStartTime(), dto.getEndTime());

        VotingSession votingSession = new VotingSession();
        votingSession.setAgenda(agenda);
        votingSession.setStartTime(dto.getStartTime());
        votingSession.setEndTime(dto.getEndTime());

        return votingSessionRepository.save(votingSession);
    }

    public Optional<VotingSession> findByIdAndAgendaId(Long id, Long agendaId) {
        return votingSessionRepository.findByIdAndAgendaId(id, agendaId);
    }

    public List<VotingSession> listAllByAgendaId(Long agendaId) {
        return votingSessionRepository.findByAgendaId(agendaId);
    }

    public VotingSession close(Long id) {
        VotingSession votingSession = votingSessionRepository.findById(id).orElseThrow(() -> new NotFoundException("Sessão não encontrada"));
//        todo: validar
        votingSession.setEndTime(LocalDateTime.now());
        votingSessionRepository.save(votingSession);
        return votingSessionRepository.save(votingSession);
    }

//    private void validateSessionTimes(LocalDateTime startTime, LocalDateTime endTime) {
//        LocalDateTime now = LocalDateTime.now();
//
//        if (startTime.isBefore(now)) {
//            throw new BusinessException("A data de início não pode estar no passado");
//        }
//
//        if (endTime.isBefore(startTime)) {
//            throw new BusinessException("A data de término deve ser posterior à de início");
//        }
//
//        Duration duration = Duration.between(startTime, endTime);
//        if (duration.toMinutes() < 1) {
//            throw new BusinessException("A sessão deve durar no mínimo 1 minuto");
//        }
//
//        if (duration.toHours() > 1) {
//            throw new BusinessException("A sessão deve durar no máximo 1 hora");
//        }
//    }
}
