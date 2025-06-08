package com.teste.assembleia.domain.entity;

import com.teste.assembleia.domain.exception.VotingSessionEndedException;
import com.teste.assembleia.domain.valueObject.VoteChoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test - Entity - VotingSession")
class VotingSessionTest {

    private VotingSession openSession;
    private VotingSession closedSession;

    @BeforeEach
    void setUp() {
        // Uma sessão que está atualmente aberta para votação
        openSession = new VotingSession();
        openSession.setId(1L);
        openSession.setStartTime(LocalDateTime.now());
        openSession.setEndTime(LocalDateTime.now().plusHours(1)); // Termina em 1 hora

        // Uma sessão que já foi encerrada
        closedSession = new VotingSession();
        closedSession.setId(2L);
        closedSession.setStartTime(LocalDateTime.now().minusHours(2));
        closedSession.setEndTime(LocalDateTime.now().minusHours(1)); // Terminou há 1 hora
    }

    @Test
    @DisplayName("Deve inicializar com valores padrão corretos ao ser criada")
    void constructor_shouldInitializeCorrectlyWhenCreated() {
        // Arrange
        VotingSession newSession = new VotingSession();

        // Act (A ação é o próprio construtor)

        // Assert
        assertFalse(newSession.getProcessed(), "Uma nova sessão não deve estar processada.");
        assertEquals(0L, newSession.getYesVotes(), "Deve iniciar com zero votos 'SIM'.");
        assertEquals(0L, newSession.getNoVotes(), "Deve iniciar com zero votos 'NÃO'.");
    }

    @Test
    @DisplayName("Deve receber um voto com sucesso quando a sessão estiver aberta")
    void receiveVote_shouldReceiveVoteWhenSessionIsOpen() {
        // Arrange
        String associateId = "associate-123";
        VoteChoice choice = VoteChoice.SIM;

        // Act
        Vote createdVote = openSession.receiveVote(associateId, choice);

        // Assert
        assertNotNull(createdVote, "O objeto de voto não deveria ser nulo.");
        assertEquals(openSession, createdVote.getVotingSession(), "O voto deve pertencer à sessão correta.");
        assertEquals(associateId, createdVote.getAssociateId(), "O ID do associado deve ser o informado.");
        assertEquals(choice, createdVote.getChoice(), "A escolha do voto deve ser a informada.");
        assertNotNull(createdVote.getTimestamp(), "O timestamp do voto deve ser preenchido.");
    }

    @Test
    @DisplayName("NÃO deve receber um voto quando a sessão já estiver encerrada")
    void receiveVote_shouldNotReceiveVoteWhenSessionIsClosed() {
        // Arrange
        String associateId = "associate-456";
        VoteChoice choice = VoteChoice.NAO;

        // Act & Assert
        VotingSessionEndedException exception = assertThrows(VotingSessionEndedException.class, () -> {
            closedSession.receiveVote(associateId, choice);
        });

        assertEquals("Tentativa de ação em sessão já encerrada.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar os resultados da votação corretamente")
    void processResults_shouldProcessResults() {
        // Arrange
        Long yesCount = 150L;
        Long noCount = 75L;
        // Garante que a sessão não está processada antes do teste
        assertFalse(openSession.getProcessed());

        // Act
        openSession.processResults(yesCount, noCount);

        // Assert
        // Verifica se o estado da sessão foi atualizado como esperado.
        assertEquals(yesCount, openSession.getYesVotes(), "A contagem de votos 'SIM' deve ser atualizada.");
        assertEquals(noCount, openSession.getNoVotes(), "A contagem de votos 'NÃO' deve ser atualizada.");
        assertTrue(openSession.getProcessed(), "A sessão deve ser marcada como processada.");
    }
}