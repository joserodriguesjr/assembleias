package com.teste.assembleia.domain.entity;

import com.teste.assembleia.domain.exception.VotingSessionEndedException;
import com.teste.assembleia.domain.valueObject.VoteChoice;
import jakarta.persistence.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "voting_session")
@Slf4j
public class VotingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voting_session_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_id", nullable = false)
    private Agenda agenda;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "yes_votes", nullable = false)
    private Long yesVotes;

    @Column(name = "no_votes", nullable = false)
    private Long noVotes;

    @Column(name = "processed", nullable = false)
    private Boolean processed;

    public VotingSession() {
        this.processed = false;
        this.yesVotes = 0L;
        this.noVotes = 0L;
    }

    public Vote receiveVote(String associateId, VoteChoice choice) {
        if (LocalDateTime.now().isAfter(this.endTime)) {
            log.warn("Falha ao registrar voto para sessão ID {}. Sessão já encerrou.", this.getId());
            throw new VotingSessionEndedException(this.endTime);
        }

        Vote vote = new Vote();
        vote.setVotingSession(this);
        vote.setAssociateId(associateId);
        vote.setChoice(choice);
        vote.setTimestamp(LocalDateTime.now());

        return vote;
    }

    public void processResults(Long yesCount, Long noCount) {
        this.setYesVotes(yesCount);
        this.setNoVotes(noCount);
        this.setProcessed(true);
    }

}
