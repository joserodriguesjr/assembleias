package com.teste.assembleia.domain.repository;

import com.teste.assembleia.domain.entity.VotingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {

    @Query("SELECT s FROM VotingSession s WHERE s.agenda.id = :agendaId")
    Optional<VotingSession> findByAgendaId(@Param("agendaId") Long agendaId);

    List<VotingSession> findAllByEndTimeBeforeAndProcessedIsFalse(LocalDateTime now);

}

