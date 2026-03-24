package br.com.nexus.change.application.api.resources;

import br.com.nexus.change.application.api.dto.response.ChangeLogResponse;
import br.com.nexus.change.application.api.mapper.ChangeLogApiMapper;
import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.core.domain.change.ChangeLog;
import br.com.nexus.change.core.ports.in.changelog.FindByIdChangeLogPort;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ChangeLogResourcesTest {

    @Mock
    private FindByIdChangeLogPort findByIdChangeLogPort;

    @Mock
    private ChangeLogApiMapper changeLogApiMapper;

    @InjectMocks
    private ChangeLogResources changeLogResources;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(changeLogResources)
                .setControllerAdvice(new TestControllerAdvice())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    private ChangeLog changeLog(UUID id, UUID changeId, String status) {
        return ChangeLog.builder()
                .id(id)
                .changeId(changeId)
                .changeStatus(status)
                .createdDate(LocalDateTime.of(2026, 3, 24, 10, 0))
                .build();
    }

    private ChangeLogResponse response(UUID id, UUID changeId, String status) {
        return ChangeLogResponse.builder()
                .id(id)
                .changeId(changeId)
                .changeStatus(status)
                .createdDate(LocalDateTime.of(2026, 3, 24, 10, 0))
                .build();
    }

    @Test
    @DisplayName("findOne should return 200 with mapped response when changelog exists")
    void findOne_shouldReturn200WhenFound() throws Exception {
        UUID id = UUID.randomUUID();
        UUID changeId = UUID.randomUUID();
        ChangeLog found = changeLog(id, changeId, "CREATED");
        ChangeLogResponse response = response(id, changeId, "CREATED");

        when(findByIdChangeLogPort.findById(id)).thenReturn(found);
        when(changeLogApiMapper.fromEntity(found)).thenReturn(response);

        mockMvc.perform(get("/v1/change-logs/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.changeId").value(changeId.toString()))
                .andExpect(jsonPath("$.changeStatus").value("CREATED"));

        verify(findByIdChangeLogPort, times(1)).findById(id);
        verify(changeLogApiMapper, times(1)).fromEntity(found);
    }

    @Test
    @DisplayName("findOne should return 400 when changelog does not exist")
    void findOne_shouldReturn400WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(findByIdChangeLogPort.findById(id)).thenReturn(null);

        mockMvc.perform(get("/v1/change-logs/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Changes não encontrado ao buscar por id"));

        verify(findByIdChangeLogPort, times(1)).findById(id);
        verify(changeLogApiMapper, never()).fromEntity(any(ChangeLog.class));
    }

    @Test
    @DisplayName("findByStatus path should return 200 with changelog list")
    void findByStatusPath_shouldReturn200WithList() throws Exception {
        UUID changeId = UUID.randomUUID();
        List<ChangeLog> list = List.of(
                changeLog(UUID.randomUUID(), changeId, "CREATED"),
                changeLog(UUID.randomUUID(), changeId, "DEPLOYED")
        );

        when(findByIdChangeLogPort.findByStatus(changeId)).thenReturn(list);

        mockMvc.perform(get("/v1/change-logs/{id}/status", changeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].changeId").value(changeId.toString()))
                .andExpect(jsonPath("$[0].changeStatus").value("CREATED"))
                .andExpect(jsonPath("$[1].changeStatus").value("DEPLOYED"));

        verify(findByIdChangeLogPort, times(1)).findByStatus(changeId);
    }

    @Test
    @DisplayName("findByStatus path should return 400 when port returns null")
    void findByStatusPath_shouldReturn400WhenNull() throws Exception {
        UUID changeId = UUID.randomUUID();
        when(findByIdChangeLogPort.findByStatus(changeId)).thenReturn(null);

        mockMvc.perform(get("/v1/change-logs/{id}/status", changeId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Changes não encontrado ao buscar por id"));

        verify(findByIdChangeLogPort, times(1)).findByStatus(changeId);
    }

    @Test
    @DisplayName("findByQueryStatus should return 200 with changelog list")
    void findByQueryStatus_shouldReturn200WithList() throws Exception {
        UUID changeId = UUID.randomUUID();
        List<ChangeLog> list = List.of(
                changeLog(UUID.randomUUID(), changeId, "VALIDATED"),
                changeLog(UUID.randomUUID(), changeId, "DEPLOYED")
        );

        when(findByIdChangeLogPort.findByStatus(changeId)).thenReturn(list);

        mockMvc.perform(get("/v1/change-logs").queryParam("changeId", changeId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].changeId").value(changeId.toString()))
                .andExpect(jsonPath("$[0].changeStatus").value("VALIDATED"))
                .andExpect(jsonPath("$[1].changeStatus").value("DEPLOYED"));

        verify(findByIdChangeLogPort, times(1)).findByStatus(changeId);
    }

    @Test
    @DisplayName("findByQueryStatus should return 400 when port returns null")
    void findByQueryStatus_shouldReturn400WhenNull() throws Exception {
        UUID changeId = UUID.randomUUID();
        when(findByIdChangeLogPort.findByStatus(changeId)).thenReturn(null);

        mockMvc.perform(get("/v1/change-logs").queryParam("changeId", changeId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Changes não encontrado ao buscar por id"));

        verify(findByIdChangeLogPort, times(1)).findByStatus(changeId);
    }

    @RestControllerAdvice
    static class TestControllerAdvice {

        @ExceptionHandler(ResourceFoundException.class)
        ProblemDetail handleResourceFound(ResourceFoundException ex) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}

