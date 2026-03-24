package br.com.nexus.change.application.api.resources;

import br.com.nexus.change.application.api.dto.request.ScheduledWindowRequest;
import br.com.nexus.change.application.api.dto.response.ScheduledWindowResponse;
import br.com.nexus.change.application.api.mapper.ScheduleWindowApiMapper;
import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.core.domain.schedule.ScheduledWindow;
import br.com.nexus.change.core.ports.in.scheduled.CreateScheduledWindowPort;
import br.com.nexus.change.core.ports.in.scheduled.DeleteScheduledWindowPort;
import br.com.nexus.change.core.ports.in.scheduled.FindByIdScheduledWindowPort;
import br.com.nexus.change.core.ports.in.scheduled.FindScheduledWindowsPort;
import br.com.nexus.change.core.ports.in.scheduled.UpdateScheduledWindowPort;
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
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ScheduledWindowResourcesTest {

    @Mock
    private CreateScheduledWindowPort createScheduledWindowPort;
    @Mock
    private DeleteScheduledWindowPort deleteScheduledWindowPort;
    @Mock
    private FindByIdScheduledWindowPort findByIdScheduledWindowPort;
    @Mock
    private FindScheduledWindowsPort findScheduledWindowsPort;
    @Mock
    private UpdateScheduledWindowPort updateScheduledWindowPort;
    @Mock
    private ScheduleWindowApiMapper scheduleWindowApiMapper;

    @InjectMocks
    private ScheduledWindowResources scheduledWindowResources;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(scheduledWindowResources)
                .setControllerAdvice(new TestControllerAdvice())
                .setValidator(validator)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    private ScheduledWindowRequest request(String responsible, UUID changeId) {
        return ScheduledWindowRequest.builder()
                .responsible(responsible)
                .start(LocalDateTime.of(2026, 3, 25, 22, 0))
                .end(LocalDateTime.of(2026, 3, 26, 0, 0))
                .changeId(changeId)
                .build();
    }

    private ScheduledWindow model(UUID id, String responsible, UUID changeId) {
        return ScheduledWindow.builder()
                .id(id)
                .responsible(responsible)
                .start(LocalDateTime.of(2026, 3, 25, 22, 0))
                .end(LocalDateTime.of(2026, 3, 26, 0, 0))
                .changeId(changeId)
                .build();
    }

    private ScheduledWindowResponse response(UUID id, String responsible, UUID changeId) {
        return ScheduledWindowResponse.builder()
                .id(id)
                .responsible(responsible)
                .start(LocalDateTime.of(2026, 3, 25, 22, 0))
                .end(LocalDateTime.of(2026, 3, 26, 0, 0))
                .changeId(changeId)
                .build();
    }

    private String requestJson(String responsible, UUID changeId) {
        return "{" +
                "\"responsible\":\"" + responsible + "\"," +
                "\"start\":\"2026-03-25T22:00:00.000Z\"," +
                "\"end\":\"2026-03-26T00:00:00.000Z\"," +
                "\"changeId\":\"" + changeId + "\"" +
                "}";
    }

    @Test
    @DisplayName("save should return 201 with location and body")
    void save_shouldReturn201WithLocationAndBody() throws Exception {
        UUID id = UUID.randomUUID();
        UUID changeId = UUID.randomUUID();
        ScheduledWindowRequest request = request("time-infra@nexus.com", changeId);
        ScheduledWindow mapped = model(null, "time-infra@nexus.com", changeId);
        ScheduledWindow saved = model(id, "time-infra@nexus.com", changeId);
        ScheduledWindowResponse response = response(id, "time-infra@nexus.com", changeId);

        when(scheduleWindowApiMapper.fromRequest(any(ScheduledWindowRequest.class))).thenReturn(mapped);
        when(createScheduledWindowPort.save(mapped)).thenReturn(saved);
        when(scheduleWindowApiMapper.fromEntity(saved)).thenReturn(response);

        mockMvc.perform(post("/v1/scheduleds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("time-infra@nexus.com", changeId)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/v1/scheduleds/" + id))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.responsible").value("time-infra@nexus.com"))
                .andExpect(jsonPath("$.changeId").value(changeId.toString()));

        verify(scheduleWindowApiMapper, times(1)).fromRequest(any(ScheduledWindowRequest.class));
        verify(createScheduledWindowPort, times(1)).save(mapped);
        verify(scheduleWindowApiMapper, times(1)).fromEntity(saved);
    }

    @Test
    @DisplayName("save should return 400 when port returns null")
    void save_shouldReturn400WhenPortReturnsNull() throws Exception {
        UUID changeId = UUID.randomUUID();
        ScheduledWindowRequest request = request("time-infra@nexus.com", changeId);
        ScheduledWindow mapped = model(null, "time-infra@nexus.com", changeId);

        when(scheduleWindowApiMapper.fromRequest(any(ScheduledWindowRequest.class))).thenReturn(mapped);
        when(createScheduledWindowPort.save(mapped)).thenReturn(null);

        mockMvc.perform(post("/v1/scheduleds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("time-infra@nexus.com", changeId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Schedule não encontroado ao cadastrar"));

        verify(scheduleWindowApiMapper, times(1)).fromRequest(any(ScheduledWindowRequest.class));
        verify(createScheduledWindowPort, times(1)).save(mapped);
    }

    @Test
    @DisplayName("save should return 404 when request is invalid")
    void save_shouldReturn404WhenRequestIsInvalid() throws Exception {
        ScheduledWindowRequest invalid = ScheduledWindowRequest.builder()
                .responsible(null)
                .start(null)
                .end(null)
                .changeId(null)
                .build();

        mockMvc.perform(post("/v1/scheduleds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").exists());

        verify(scheduleWindowApiMapper, never()).fromRequest(any(ScheduledWindowRequest.class));
        verify(createScheduledWindowPort, never()).save(any(ScheduledWindow.class));
    }

    @Test
    @DisplayName("update should return 200 with body")
    void update_shouldReturn200WithBody() throws Exception {
        UUID id = UUID.randomUUID();
        UUID changeId = UUID.randomUUID();
        ScheduledWindowRequest request = request("time-sec@nexus.com", changeId);
        ScheduledWindow mapped = model(null, "time-sec@nexus.com", changeId);
        ScheduledWindow updated = model(id, "time-sec@nexus.com", changeId);
        ScheduledWindowResponse response = response(id, "time-sec@nexus.com", changeId);

        when(scheduleWindowApiMapper.fromRequest(any(ScheduledWindowRequest.class))).thenReturn(mapped);
        when(updateScheduledWindowPort.update(id, mapped)).thenReturn(updated);
        when(scheduleWindowApiMapper.fromEntity(updated)).thenReturn(response);

        mockMvc.perform(put("/v1/scheduleds/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("time-sec@nexus.com", changeId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.responsible").value("time-sec@nexus.com"))
                .andExpect(jsonPath("$.changeId").value(changeId.toString()));

        verify(scheduleWindowApiMapper, times(1)).fromRequest(any(ScheduledWindowRequest.class));
        verify(updateScheduledWindowPort, times(1)).update(id, mapped);
        verify(scheduleWindowApiMapper, times(1)).fromEntity(updated);
    }

    @Test
    @DisplayName("update should return 400 when schedule window does not exist")
    void update_shouldReturn400WhenScheduleDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        UUID changeId = UUID.randomUUID();
        ScheduledWindowRequest request = request("time-sec@nexus.com", changeId);
        ScheduledWindow mapped = model(null, "time-sec@nexus.com", changeId);

        when(scheduleWindowApiMapper.fromRequest(any(ScheduledWindowRequest.class))).thenReturn(mapped);
        when(updateScheduledWindowPort.update(id, mapped)).thenReturn(null);

        mockMvc.perform(put("/v1/scheduleds/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("time-sec@nexus.com", changeId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("\"ScheduleWindow não encontroado ao atualizar"));

        verify(scheduleWindowApiMapper, times(1)).fromRequest(any(ScheduledWindowRequest.class));
        verify(updateScheduledWindowPort, times(1)).update(id, mapped);
    }

    @Test
    @DisplayName("findAll should return 200 with list when schedules exist")
    void findAll_shouldReturn200WithListWhenSchedulesExist() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID changeId1 = UUID.randomUUID();
        UUID changeId2 = UUID.randomUUID();

        List<ScheduledWindow> list = List.of(
                model(id1, "time-a@nexus.com", changeId1),
                model(id2, "time-b@nexus.com", changeId2)
        );
        List<ScheduledWindowResponse> responses = List.of(
                response(id1, "time-a@nexus.com", changeId1),
                response(id2, "time-b@nexus.com", changeId2)
        );

        when(findScheduledWindowsPort.findAll()).thenReturn(list);
        when(scheduleWindowApiMapper.map(list)).thenReturn(responses);

        mockMvc.perform(get("/v1/scheduleds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id1.toString()))
                .andExpect(jsonPath("$[0].responsible").value("time-a@nexus.com"))
                .andExpect(jsonPath("$[1].id").value(id2.toString()))
                .andExpect(jsonPath("$[1].responsible").value("time-b@nexus.com"));

        verify(findScheduledWindowsPort, times(1)).findAll();
        verify(scheduleWindowApiMapper, times(1)).map(list);
    }

    @Test
    @DisplayName("findAll should return 204 when list is empty")
    void findAll_shouldReturn204WhenListIsEmpty() throws Exception {
        when(findScheduledWindowsPort.findAll()).thenReturn(List.of());
        when(scheduleWindowApiMapper.map(List.of())).thenReturn(List.of());

        mockMvc.perform(get("/v1/scheduleds"))
                .andExpect(status().isNoContent());

        verify(findScheduledWindowsPort, times(1)).findAll();
        verify(scheduleWindowApiMapper, times(1)).map(List.of());
    }

    @Test
    @DisplayName("findOne should return 200 with body when schedule exists")
    void findOne_shouldReturn200WithBodyWhenScheduleExists() throws Exception {
        UUID id = UUID.randomUUID();
        UUID changeId = UUID.randomUUID();
        ScheduledWindow found = model(id, "time-infra@nexus.com", changeId);
        ScheduledWindowResponse response = response(id, "time-infra@nexus.com", changeId);

        when(findByIdScheduledWindowPort.findById(id)).thenReturn(found);
        when(scheduleWindowApiMapper.fromEntity(found)).thenReturn(response);

        mockMvc.perform(get("/v1/scheduleds/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.responsible").value("time-infra@nexus.com"))
                .andExpect(jsonPath("$.changeId").value(changeId.toString()));

        verify(findByIdScheduledWindowPort, times(1)).findById(id);
        verify(scheduleWindowApiMapper, times(1)).fromEntity(found);
    }

    @Test
    @DisplayName("findOne should return 400 when schedule does not exist")
    void findOne_shouldReturn400WhenScheduleDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(findByIdScheduledWindowPort.findById(id)).thenReturn(null);

        mockMvc.perform(get("/v1/scheduleds/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("ScheduleWindow não encontrado ao buscar por código"));

        verify(findByIdScheduledWindowPort, times(1)).findById(id);
        verify(scheduleWindowApiMapper, never()).fromEntity(any(ScheduledWindow.class));
    }

    @Test
    @DisplayName("remove should return 204")
    void remove_shouldReturn204() throws Exception {
        UUID id = UUID.randomUUID();
        when(deleteScheduledWindowPort.remove(id)).thenReturn(true);

        mockMvc.perform(delete("/v1/scheduleds/{id}", id))
                .andExpect(status().isNoContent());

        verify(deleteScheduledWindowPort, times(1)).remove(id);
    }

    @RestControllerAdvice
    static class TestControllerAdvice {

        @ExceptionHandler(ResourceFoundException.class)
        ProblemDetail handleResourceFound(ResourceFoundException ex) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
            String detail = ex.getBindingResult().getFieldErrors().stream()
                    .map(error -> String.valueOf(error.getDefaultMessage()))
                    .findFirst()
                    .orElse("Validation error");
            return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detail);
        }
    }
}

