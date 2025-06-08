package com.teste.assembleia.domain.entity;

import com.teste.assembleia.domain.valueObject.VoteChoice;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(
//        todo: check perfomance
//        indexes = {
//                @Index(name = "idx_vote_session_choice", columnList = "voting_session_id, choice")
//        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vote_session_associate", columnNames = {"voting_session_id", "associate_id"})
        }
)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "voting_session_id")
    private VotingSession votingSession;

    @Column(name = "associate_id", nullable = false)
    private String associateId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteChoice choice;

    @Column(name = "voted_at", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();


}