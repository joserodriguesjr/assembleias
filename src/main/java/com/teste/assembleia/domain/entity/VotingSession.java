package com.teste.assembleia.domain.entity;

import com.teste.assembleia.domain.exception.VotingSessionEndedException;
import com.teste.assembleia.domain.valueObject.VoteChoice;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "voting_session")
public class VotingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voting_session_id")
    private Long id;

    @ManyToOne
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

    /**
     * Valida e cria um novo voto para esta sessão.
     *
     * @param associateId O ID do associado que está votando.
     * @param choice      A escolha do voto (SIM/NAO).
     * @return Uma nova instância de Vote.
     */
    public Vote receiveVote(String associateId, VoteChoice choice) {
        if (LocalDateTime.now().isAfter(this.endTime)) {
            throw new VotingSessionEndedException("A sessão de votação já foi encerrada.");
        }

        Vote vote = new Vote();
        vote.setVotingSession(this);
        vote.setAssociateId(associateId);
        vote.setChoice(choice);
        vote.setTimestamp(LocalDateTime.now());

        return vote;
    }

    /**
     * Processa o resultado final da votação.
     *
     * @param yesCount Total de votos 'SIM'.
     * @param noCount  Total de votos 'NÃO'.
     */
    public void processResults(Long yesCount, Long noCount) {
        this.setYesVotes(yesCount);
        this.setNoVotes(noCount);
        this.setProcessed(true);
    }

}
