package br.com.nexus.change.application.api.resources;

import br.com.nexus.change.application.api.dto.request.ChangeRequest;
import br.com.nexus.change.application.api.dto.response.ChangeResponse;
import br.com.nexus.change.application.api.mapper.ChangeApiMapper;
import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.core.domain.change.Change;
import br.com.nexus.change.core.ports.in.change.CreateChangePort;
import br.com.nexus.change.core.ports.in.change.DeleteChangePort;
import br.com.nexus.change.core.ports.in.change.FindByIdChangePort;
import br.com.nexus.change.core.ports.in.change.FindChangesPort;
import br.com.nexus.change.core.ports.in.change.UpdateChangePort;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class ChangeResourcesTest {

    @Mock
    private DeleteChangePort deleteChangePort;
    @Mock
    private FindByIdChangePort findByIdChangePort;
    @Mock
    private FindChangesPort findChangesPort;
    @Mock
    private UpdateChangePort updateChangePort;
    @Mock
    private CreateChangePort createChangePort;
    @Mock
    private ChangeApiMapper changeApiMapper;

    @InjectMocks
    private ChangeResources changeResources;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(changeResources)
                .setControllerAdvice(new TestControllerAdvice())
                .setValidator(validator)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    private ChangeRequest request(String title, UUID componentId, String status) {
        return ChangeRequest.builder()
                .title(title)
                .description("Descricao de " + title)
                .componentId(componentId)
                .environment("PROD")
                .changeType("NORMAL")
                .changeStatus(status)
                .requestBy("user@nexus.com")
                .build();
    }

    private Change change(UUID id, String title, UUID componentId, String status) {
        return Change.builder()
                .id(id)
                .title(title)
                .description("Descricao de " + title)
                .componentId(componentId)
                .environment("PROD")
                .changeType("NORMAL")
                .changeStatus(status)
                .requestBy("user@nexus.com")
                .build();
    }

    private ChangeResponse response(UUID id, String title, UUID componentId, String status) {
        return ChangeResponse.builder()
                .id(id)
                .title(title)
                .description("Descricao de " + title)
                .componentId(componentId)
                .environment("PROD")
                .changeType("NORMAL")
                .changeStatus(status)
                .requestBy("user@nexus.com")
                .build();
    }

    @Test
    @DisplayName("save should return 201 with location and body")
    void save_shouldReturn201WithLocationAndBody() throws Exception {
        UUID componentId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        ChangeRequest request = request("Deploy API", componentId, "DRAFT");
        Change mapped = change(null, "Deploy API", componentId, "DRAFT");
        Change saved = change(id, "Deploy API", componentId, "DRAFT");
        ChangeResponse response = response(id, "Deploy API", componentId, "DRAFT");

        when(changeApiMapper.fromRequest(any(ChangeRequest.class))).thenReturn(mapped);
        when(createChangePort.save(mapped)).thenReturn(saved);
        when(changeApiMapper.fromEntity(saved)).thenReturn(response);

        mockMvc.perform(post("/v1/changes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/v1/changes/" + id))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("Deploy API"))
                .andExpect(jsonPath("$.componentId").value(componentId.toString()))
                .andExpect(jsonPath("$.changeStatus").value("DRAFT"));

        verify(changeApiMapper, times(1)).fromRequest(any(ChangeRequest.class));
        verify(createChangePort, times(1)).save(mapped);
        verify(changeApiMapper, times(1)).fromEntity(saved);
    }

    @Test
    @DisplayName("save should return 400 when port returns null")
    void save_shouldReturn400WhenPortReturnsNull() throws Exception {
        UUID componentId = UUID.randomUUID();
        ChangeRequest request = request("Deploy API", componentId, "DRAFT");
        Change mapped = change(null, "Deploy API", componentId, "DRAFT");

        when(changeApiMapper.fromRequest(any(ChangeRequest.class))).thenReturn(mapped);
        when(createChangePort.save(mapped)).thenReturn(null);

        mockMvc.perform(post("/v1/changes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Change não encontroado ao cadastrar"));

        verify(changeApiMapper, times(1)).fromRequest(any(ChangeRequest.class));
        verify(createChangePort, times(1)).save(mapped);
    }

    @Test
    @DisplayName("save should return 404 when request is invalid")
    void save_shouldReturn404WhenRequestIsInvalid() throws Exception {
        ChangeRequest invalid = ChangeRequest.builder()
                .title("ab")
                .description("Descricao")
                .environment("PROD")
                .changeType("NORMAL")
                .requestBy("user@nexus.com")
                .build();

        mockMvc.perform(post("/v1/changes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").exists());

        verify(changeApiMapper, never()).fromRequest(any(ChangeRequest.class));
        verify(createChangePort, never()).save(any(Change.class));
    }

    @Test
    @DisplayName("update should return 200 with body")
    void update_shouldReturn200WithBody() throws Exception {
        UUID id = UUID.randomUUID();
        UUID componentId = UUID.randomUUID();
        ChangeRequest request = request("Deploy API v2", componentId, "VALIDATED");
        Change mapped = change(null, "Deploy API v2", componentId, "VALIDATED");
        Change updated = change(id, "Deploy API v2", componentId, "VALIDATED");
        ChangeResponse response = response(id, "Deploy API v2", componentId, "VALIDATED");

        when(changeApiMapper.fromRequest(any(ChangeRequest.class))).thenReturn(mapped);
        when(updateChangePort.update(id, mapped)).thenReturn(updated);
        when(changeApiMapper.fromEntity(updated)).thenReturn(response);

        mockMvc.perform(put("/v1/changes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("Deploy API v2"))
                .andExpect(jsonPath("$.changeStatus").value("VALIDATED"));

        verify(changeApiMapper, times(1)).fromRequest(any(ChangeRequest.class));
        verify(updateChangePort, times(1)).update(id, mapped);
        verify(changeApiMapper, times(1)).fromEntity(updated);
    }

    @Test
    @DisplayName("update should return 400 when change does not exist")
    void update_shouldReturn400WhenChangeDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        UUID componentId = UUID.randomUUID();
        ChangeRequest request = request("Deploy API v2", componentId, "VALIDATED");
        Change mapped = change(null, "Deploy API v2", componentId, "VALIDATED");

        when(changeApiMapper.fromRequest(any(ChangeRequest.class))).thenReturn(mapped);
        when(updateChangePort.update(id, mapped)).thenReturn(null);

        mockMvc.perform(put("/v1/changes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Change não encontroado ao atualizar"));

        verify(changeApiMapper, times(1)).fromRequest(any(ChangeRequest.class));
        verify(updateChangePort, times(1)).update(id, mapped);
    }

    @Test
    @DisplayName("updateStatus should return 200 with body")
    void updateStatus_shouldReturn200WithBody() throws Exception {
        UUID id = UUID.randomUUID();
        UUID componentId = UUID.randomUUID();
        Change updated = change(id, "Deploy API", componentId, "DEPLOYED");
        ChangeResponse response = response(id, "Deploy API", componentId, "DEPLOYED");

        when(updateChangePort.updateStatusById(id, "DEPLOYED")).thenReturn(updated);
        when(changeApiMapper.fromEntity(updated)).thenReturn(response);

        mockMvc.perform(put("/v1/changes/{changeId}/status/{status}", id, "DEPLOYED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.changeStatus").value("DEPLOYED"));

        verify(updateChangePort, times(1)).updateStatusById(id, "DEPLOYED");
        verify(changeApiMapper, times(1)).fromEntity(updated);
    }

    @Test
    @DisplayName("updateStatus should return 400 when change does not exist")
    void updateStatus_shouldReturn400WhenChangeDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(updateChangePort.updateStatusById(id, "DEPLOYED")).thenReturn(null);

        mockMvc.perform(put("/v1/changes/{changeId}/status/{status}", id, "DEPLOYED"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Change não encontroado ao atualizar"));

        verify(updateChangePort, times(1)).updateStatusById(id, "DEPLOYED");
        verify(changeApiMapper, never()).fromEntity(any(Change.class));
    }

    @Test
    @DisplayName("findAll should return 200 with list when changes exist")
    void findAll_shouldReturn200WithListWhenChangesExist() throws Exception {
        UUID componentId = UUID.randomUUID();
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<Change> changes = List.of(
                change(id1, "Deploy API", componentId, "CREATED"),
                change(id2, "Rollback API", componentId, "ROLLBACK")
        );
        List<ChangeResponse> responses = List.of(
                response(id1, "Deploy API", componentId, "CREATED"),
                response(id2, "Rollback API", componentId, "ROLLBACK")
        );

        when(findChangesPort.findAll()).thenReturn(changes);
        when(changeApiMapper.map(changes)).thenReturn(responses);

        mockMvc.perform(get("/v1/changes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id1.toString()))
                .andExpect(jsonPath("$[0].title").value("Deploy API"))
                .andExpect(jsonPath("$[1].id").value(id2.toString()))
                .andExpect(jsonPath("$[1].title").value("Rollback API"));

        verify(findChangesPort, times(1)).findAll();
        verify(changeApiMapper, times(1)).map(changes);
    }

    @Test
    @DisplayName("findAll should return 204 when list is empty")
    void findAll_shouldReturn204WhenListIsEmpty() throws Exception {
        when(findChangesPort.findAll()).thenReturn(List.of());
        when(changeApiMapper.map(List.of())).thenReturn(List.of());

        mockMvc.perform(get("/v1/changes"))
                .andExpect(status().isNoContent());

        verify(findChangesPort, times(1)).findAll();
        verify(changeApiMapper, times(1)).map(List.of());
    }

    @Test
    @DisplayName("findOne should return 200 with body when change exists")
    void findOne_shouldReturn200WithBodyWhenChangeExists() throws Exception {
        UUID id = UUID.randomUUID();
        UUID componentId = UUID.randomUUID();
        Change found = change(id, "Deploy API", componentId, "CREATED");
        ChangeResponse response = response(id, "Deploy API", componentId, "CREATED");

        when(findByIdChangePort.findById(id)).thenReturn(found);
        when(changeApiMapper.fromEntity(found)).thenReturn(response);

        mockMvc.perform(get("/v1/changes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("Deploy API"))
                .andExpect(jsonPath("$.changeStatus").value("CREATED"));

        verify(findByIdChangePort, times(1)).findById(id);
        verify(changeApiMapper, times(1)).fromEntity(found);
    }

    @Test
    @DisplayName("findOne should return 400 when change does not exist")
    void findOne_shouldReturn400WhenChangeDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(findByIdChangePort.findById(id)).thenReturn(null);

        mockMvc.perform(get("/v1/changes/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Changes não encontrado ao buscar por id"));

        verify(findByIdChangePort, times(1)).findById(id);
        verify(changeApiMapper, never()).fromEntity(any(Change.class));
    }

    @Test
    @DisplayName("remove should return 204")
    void remove_shouldReturn204() throws Exception {
        UUID id = UUID.randomUUID();
        when(deleteChangePort.remove(id)).thenReturn(true);

        mockMvc.perform(delete("/v1/changes/{id}", id))
                .andExpect(status().isNoContent());

        verify(deleteChangePort, times(1)).remove(id);
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

