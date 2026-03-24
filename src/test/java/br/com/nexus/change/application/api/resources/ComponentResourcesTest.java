package br.com.nexus.change.application.api.resources;

import br.com.nexus.change.application.api.dto.request.ComponentRequest;
import br.com.nexus.change.application.api.dto.response.ComponentResponse;
import br.com.nexus.change.application.api.mapper.ComponentApiMapper;
import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.core.domain.component.ChangeComponent;
import br.com.nexus.change.core.ports.in.component.CreateComponentPort;
import br.com.nexus.change.core.ports.in.component.DeleteComponentPort;
import br.com.nexus.change.core.ports.in.component.FindByIdComponentsPort;
import br.com.nexus.change.core.ports.in.component.FindComponentPort;
import br.com.nexus.change.core.ports.in.component.UpdateComponentPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
class ComponentResourcesTest {

    @Mock
    private CreateComponentPort createCompomentPort;
    @Mock
    private DeleteComponentPort deleteCompomentPort;
    @Mock
    private FindByIdComponentsPort findByIdCompomentPort;
    @Mock
    private FindComponentPort findCompomentsPort;
    @Mock
    private UpdateComponentPort updateCompomentPort;
    @Mock
    private ComponentApiMapper componentApiMapper;

    @InjectMocks
    private ComponentResources componentResources;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(componentResources)
                .setControllerAdvice(new TestControllerAdvice())
                .setValidator(validator)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    private ComponentRequest request(String name, String version) {
        return ComponentRequest.builder()
                .name(name)
                .version(version)
                .build();
    }

    private ChangeComponent component(UUID id, String name, String version) {
        return ChangeComponent.builder()
                .id(id)
                .name(name)
                .version(version)
                .build();
    }

    private ComponentResponse response(UUID id, String name, String version) {
        return ComponentResponse.builder()
                .id(id)
                .name(name)
                .version(version)
                .build();
    }

    @Test
    @DisplayName("save should return 201 with location and body")
    void save_shouldReturn201WithLocationAndBody() throws Exception {
        ComponentRequest request = request("core-api", "1.0.0");
        ChangeComponent mapped = component(null, "core-api", "1.0.0");
        UUID id = UUID.randomUUID();
        ChangeComponent saved = component(id, "core-api", "1.0.0");
        ComponentResponse response = response(id, "core-api", "1.0.0");

        when(componentApiMapper.fromRequest(any(ComponentRequest.class))).thenReturn(mapped);
        when(createCompomentPort.save(mapped)).thenReturn(saved);
        when(componentApiMapper.fromEntity(saved)).thenReturn(response);

        mockMvc.perform(post("/v1/components")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/v1/components/" + id))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("core-api"))
                .andExpect(jsonPath("$.version").value("1.0.0"));

        verify(componentApiMapper, times(1)).fromRequest(any(ComponentRequest.class));
        verify(createCompomentPort, times(1)).save(mapped);
        verify(componentApiMapper, times(1)).fromEntity(saved);
    }

    @Test
    @DisplayName("save should return 400 when port returns null")
    void save_shouldReturn400WhenPortReturnsNull() throws Exception {
        ComponentRequest request = request("core-api", "1.0.0");
        ChangeComponent mapped = component(null, "core-api", "1.0.0");

        when(componentApiMapper.fromRequest(any(ComponentRequest.class))).thenReturn(mapped);
        when(createCompomentPort.save(mapped)).thenReturn(null);

        mockMvc.perform(post("/v1/components")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Compoment não encontroado ao cadastrar"));

        verify(componentApiMapper, times(1)).fromRequest(any(ComponentRequest.class));
        verify(createCompomentPort, times(1)).save(mapped);
    }

    @Test
    @DisplayName("save should return 404 when request is invalid")
    void save_shouldReturn404WhenRequestIsInvalid() throws Exception {
        ComponentRequest request = request("ab", "1.0.0");

        mockMvc.perform(post("/v1/components")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").exists());

        verify(componentApiMapper, never()).fromRequest(any(ComponentRequest.class));
        verify(createCompomentPort, never()).save(any(ChangeComponent.class));
    }

    @Test
    @DisplayName("update should return 200 with body")
    void update_shouldReturn200WithBody() throws Exception {
        UUID id = UUID.randomUUID();
        ComponentRequest request = request("core-api-v2", "2.0.0");
        ChangeComponent mapped = component(null, "core-api-v2", "2.0.0");
        ChangeComponent updated = component(id, "core-api-v2", "2.0.0");
        ComponentResponse response = response(id, "core-api-v2", "2.0.0");

        when(componentApiMapper.fromRequest(any(ComponentRequest.class))).thenReturn(mapped);
        when(updateCompomentPort.update(id, mapped)).thenReturn(updated);
        when(componentApiMapper.fromEntity(updated)).thenReturn(response);

        mockMvc.perform(put("/v1/components/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("core-api-v2"))
                .andExpect(jsonPath("$.version").value("2.0.0"));

        verify(componentApiMapper, times(1)).fromRequest(any(ComponentRequest.class));
        verify(updateCompomentPort, times(1)).update(id, mapped);
        verify(componentApiMapper, times(1)).fromEntity(updated);
    }

    @Test
    @DisplayName("update should return 400 when component does not exist")
    void update_shouldReturn400WhenComponentDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        ComponentRequest request = request("core-api-v2", "2.0.0");
        ChangeComponent mapped = component(null, "core-api-v2", "2.0.0");

        when(componentApiMapper.fromRequest(any(ComponentRequest.class))).thenReturn(mapped);
        when(updateCompomentPort.update(id, mapped)).thenReturn(null);

        mockMvc.perform(put("/v1/components/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("\"Produto não encontroado ao atualizar"));

        verify(componentApiMapper, times(1)).fromRequest(any(ComponentRequest.class));
        verify(updateCompomentPort, times(1)).update(id, mapped);
    }

    @Test
    @DisplayName("findAll should return 200 with list when components exist")
    void findAll_shouldReturn200WithListWhenComponentsExist() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<ChangeComponent> components = List.of(
                component(id1, "core-api", "1.0.0"),
                component(id2, "payments", "2.0.0")
        );
        List<ComponentResponse> responses = List.of(
                response(id1, "core-api", "1.0.0"),
                response(id2, "payments", "2.0.0")
        );

        when(findCompomentsPort.findAll()).thenReturn(components);
        when(componentApiMapper.map(components)).thenReturn(responses);

        mockMvc.perform(get("/v1/components"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id1.toString()))
                .andExpect(jsonPath("$[0].name").value("core-api"))
                .andExpect(jsonPath("$[1].id").value(id2.toString()))
                .andExpect(jsonPath("$[1].name").value("payments"));

        verify(findCompomentsPort, times(1)).findAll();
        verify(componentApiMapper, times(1)).map(components);
    }

    @Test
    @DisplayName("findAll should return 204 when list is empty")
    void findAll_shouldReturn204WhenListIsEmpty() throws Exception {
        when(findCompomentsPort.findAll()).thenReturn(List.of());
        when(componentApiMapper.map(List.of())).thenReturn(List.of());

        mockMvc.perform(get("/v1/components"))
                .andExpect(status().isNoContent());

        verify(findCompomentsPort, times(1)).findAll();
        verify(componentApiMapper, times(1)).map(List.of());
    }

    @Test
    @DisplayName("findOne should return 200 with body when component exists")
    void findOne_shouldReturn200WithBodyWhenComponentExists() throws Exception {
        UUID id = UUID.randomUUID();
        ChangeComponent found = component(id, "core-api", "1.0.0");
        ComponentResponse response = response(id, "core-api", "1.0.0");

        when(findByIdCompomentPort.findById(id)).thenReturn(found);
        when(componentApiMapper.fromEntity(found)).thenReturn(response);

        mockMvc.perform(get("/v1/components/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("core-api"))
                .andExpect(jsonPath("$.version").value("1.0.0"));

        verify(findByIdCompomentPort, times(1)).findById(id);
        verify(componentApiMapper, times(1)).fromEntity(found);
    }

    @Test
    @DisplayName("findOne should return 400 when component does not exist")
    void findOne_shouldReturn400WhenComponentDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(findByIdCompomentPort.findById(id)).thenReturn(null);

        mockMvc.perform(get("/v1/components/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Produto não encontrado ao buscar por código"));

        verify(findByIdCompomentPort, times(1)).findById(id);
        verify(componentApiMapper, never()).fromEntity(any(ChangeComponent.class));
    }

    @Test
    @DisplayName("remove should return 204")
    void remove_shouldReturn204() throws Exception {
        UUID id = UUID.randomUUID();
        when(deleteCompomentPort.remove(id)).thenReturn(true);

        mockMvc.perform(delete("/v1/components/{id}", id))
                .andExpect(status().isNoContent());

        verify(deleteCompomentPort, times(1)).remove(id);
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
                    .map(error -> error.getDefaultMessage())
                    .findFirst()
                    .orElse("Validation error");
            return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detail);
        }
    }
}

