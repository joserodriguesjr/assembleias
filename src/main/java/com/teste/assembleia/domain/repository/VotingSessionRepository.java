package com.teste.assembleia.domain.repository;

import com.teste.assembleia.domain.entity.VotingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {

    Optional<VotingSession> findByAgendaId(Long agendaId);

}

