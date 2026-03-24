package br.com.nexus.change.application.api.resources;

import br.com.nexus.change.application.api.dto.response.ChangeStatusResponse;
import br.com.nexus.change.application.api.dto.response.LogEntry;
import br.com.nexus.change.application.api.dto.response.TimelineEvent;
import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.core.service.ChangeStatusService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ChangeStatusResourcesTest {

    @Mock
    private ChangeStatusService changeStatusService;

    @InjectMocks
    private ChangeStatusResources changeStatusResources;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(changeStatusResources)
                .setControllerAdvice(new TestControllerAdvice())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    private ChangeStatusResponse response(UUID changeId) {
        return ChangeStatusResponse.builder()
                .changeId(changeId)
                .changeStatus("DEPLOYED")
                .updatedAt(Instant.parse("2026-03-24T18:00:00Z"))
                .timeline(List.of(
                        TimelineEvent.builder()
                                .event("CREATED")
                                .timestamp(Instant.parse("2026-03-24T17:00:00Z"))
                                .build()
                ))
                .log_change(List.of(
                        LogEntry.builder()
                                .timestamp(Instant.parse("2026-03-24T17:30:00Z"))
                                .level("INFO")
                                .message("Status atualizado para DEPLOYED")
                                .build()
                ))
                .build();
    }

    @Test
    @DisplayName("getStatus should return 200 with status payload")
    void getStatus_shouldReturn200WithPayload() throws Exception {
        UUID changeId = UUID.randomUUID();
        ChangeStatusResponse response = response(changeId);

        when(changeStatusService.getStatus(changeId)).thenReturn(response);

        mockMvc.perform(get("/v1/changes/{changeId}/status", changeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.changeId").value(changeId.toString()))
                .andExpect(jsonPath("$.changeStatus").value("DEPLOYED"))
                .andExpect(jsonPath("$.updatedAt").value("2026-03-24T18:00:00Z"))
                .andExpect(jsonPath("$.timeline[0].event").value("CREATED"))
                .andExpect(jsonPath("$.log_change[0].level").value("INFO"));

        verify(changeStatusService, times(1)).getStatus(changeId);
    }

    @Test
    @DisplayName("getStatus should return 400 when change is not found")
    void getStatus_shouldReturn400WhenNotFound() throws Exception {
        UUID changeId = UUID.randomUUID();
        when(changeStatusService.getStatus(changeId))
                .thenThrow(new ResourceFoundException("Change não encontrada para o id informado"));

        mockMvc.perform(get("/v1/changes/{changeId}/status", changeId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Change não encontrada para o id informado"));

        verify(changeStatusService, times(1)).getStatus(changeId);
    }

    @RestControllerAdvice
    static class TestControllerAdvice {

        @ExceptionHandler(ResourceFoundException.class)
        ProblemDetail handleResourceFound(ResourceFoundException ex) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}

