package com.teste.assembleia.repository;

import com.teste.assembleia.model.VotingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {

    List<VotingSession> findByAgendaId(Long agendaId);

    Optional<VotingSession> findByIdAndAgendaId(Long id, Long agendaId);

}

