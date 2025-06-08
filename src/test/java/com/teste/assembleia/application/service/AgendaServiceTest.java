package com.teste.assembleia.application.service;

import com.teste.assembleia.application.dto.AgendaDetailsDTO;
import com.teste.assembleia.domain.entity.Agenda;
import com.teste.assembleia.domain.entity.VotingSession;
import com.teste.assembleia.domain.exception.ResourceNotFoundException;
import com.teste.assembleia.domain.repository.AgendaRepository;
import com.teste.assembleia.domain.repository.VotingSessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test - Service - AgendaService")
class AgendaServiceTest {

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private VotingSessionRepository votingSessionRepository;

    @InjectMocks
    private AgendaService agendaService;

    @Test
    @DisplayName("Deve criar uma nova pauta com sucesso")
    void create_shouldCreateAgendaSuccessfully() {
        // Arrange
        Agenda newAgenda = new Agenda();
        newAgenda.setName("Nova Pauta");

        when(agendaRepository.save(any(Agenda.class))).thenReturn(newAgenda);

        // Act
        Agenda savedAgenda = agendaService.create(newAgenda.getName());

        // Assert
        assertNotNull(savedAgenda);
        assertEquals("Nova Pauta", savedAgenda.getName());
        verify(agendaRepository, times(1)).save(newAgenda);
    }

    @Test
    @DisplayName("Deve encontrar uma pauta pelo ID quando ela existir")
    void findById_shouldFindAgendaWhenIdExists() {
        // Arrange
        Agenda existingAgenda = new Agenda();
        existingAgenda.setId(1L);
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(existingAgenda));

        // Act
        Agenda foundAgenda = agendaService.findById(1L);

        // Assert
        assertNotNull(foundAgenda);
        assertEquals(1L, foundAgenda.getId());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar pauta por ID que não existe")
    void findById_shouldThrownExceptionWhenSearchNonexistentAgenda() {
        // Arrange
        when(agendaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            agendaService.findById(99L);
        });
    }

    @Test
    @DisplayName("Deve retornar DTO com detalhes da pauta e da sessão quando ambos existem")
    void getAgendaWithSessionDetails_shouldReturnDtoWithAgendaAndSession() {
        // Arrange
        Agenda agenda = new Agenda();
        agenda.setId(1L);
        agenda.setName("Pauta com Sessão");

        VotingSession session = new VotingSession();
        session.setId(10L);
        session.setEndTime(LocalDateTime.now().plusHours(1));

        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));
        when(votingSessionRepository.findByAgendaId(1L)).thenReturn(Optional.of(session));

        // Act
        AgendaDetailsDTO resultDto = agendaService.getAgendaWithSessionDetails(1L);

        // Assert
        assertNotNull(resultDto);
        assertEquals(1L, resultDto.getAgendaId());
        assertEquals("Pauta com Sessão", resultDto.getName());
        assertNotNull(resultDto.getSession(), "Os detalhes da sessão não deveriam ser nulos.");
        assertEquals(10L, resultDto.getSession().getId());
    }

    @Test
    @DisplayName("Deve retornar DTO com detalhes da pauta e sessão nula quando a sessão não existe")
    void getAgendaWithSessionDetails_shouldReturnDtoWithNullSession() {
        // Arrange
        Agenda agenda = new Agenda();
        agenda.setId(1L);
        agenda.setName("Pauta sem Sessão");

        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));
        when(votingSessionRepository.findByAgendaId(1L)).thenReturn(Optional.empty());

        // Act
        AgendaDetailsDTO resultDto = agendaService.getAgendaWithSessionDetails(1L);

        // Assert
        assertNotNull(resultDto);
        assertEquals(1L, resultDto.getAgendaId());
        assertNull(resultDto.getSession(), "Os detalhes da sessão deveriam ser nulos.");
    }
}