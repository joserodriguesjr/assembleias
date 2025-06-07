package com.teste.assembleia.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"voting_session_id", "associate_id"})
})
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