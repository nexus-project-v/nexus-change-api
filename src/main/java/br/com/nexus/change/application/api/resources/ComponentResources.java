package br.com.nexus.change.application.api.resources;

import br.com.nexus.change.application.api.dto.request.ComponentRequest;
import br.com.nexus.change.application.api.dto.response.ComponentResponse;
import br.com.nexus.change.application.api.mapper.ComponentApiMapper;
import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.commons.util.RestUtils;
import br.com.nexus.change.core.domain.component.ChangeComponent;
import br.com.nexus.change.core.ports.in.component.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/components")
@CrossOrigin(origins = "*", allowedHeaders = "Content-Type, Authorization", maxAge = 3600)
public class ComponentResources {

    private final CreateComponentPort createCompomentPort;
    private final DeleteComponentPort deleteCompomentPort;
    private final FindByIdComponentsPort findByIdCompomentPort;
    private final FindComponentPort findCompomentsPort;
    private final UpdateComponentPort updateCompomentPort;
    private final ComponentApiMapper componentApiMapper;

    @Autowired
    public ComponentResources(CreateComponentPort createCompomentPort, DeleteComponentPort deleteCompomentPort, FindByIdComponentsPort findByIdCompomentPort, FindComponentPort findCompomentsPort, UpdateComponentPort updateCompomentPort, ComponentApiMapper componentApiMapper) {
        this.createCompomentPort = createCompomentPort;
        this.deleteCompomentPort = deleteCompomentPort;
        this.findByIdCompomentPort = findByIdCompomentPort;
        this.findCompomentsPort = findCompomentsPort;
        this.updateCompomentPort = updateCompomentPort;
        this.componentApiMapper = componentApiMapper;
    }

    @Operation(summary = "Create a new Compoment", tags = {"productCategorys", "post"})
    @ApiResponse(responseCode = "201", content = {
            @Content(schema = @Schema(implementation = ComponentResources.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ComponentResponse> save(@Valid @RequestBody ComponentRequest request) {
        log.info("Chegada do objeto para ser salvo {}", request);
        ChangeComponent productCategory = componentApiMapper.fromRequest(request);
        ChangeComponent saved = createCompomentPort.save(productCategory);
        if (saved == null) {
            throw new ResourceFoundException("Produto não encontroado ao cadastrar");
        }

        ComponentResponse productCategoryResponse = componentApiMapper.fromEntity(saved);
        URI location = RestUtils.getUri(productCategoryResponse.getId());
        return ResponseEntity.created(location).body(productCategoryResponse);
    }

    @Operation(summary = "Update a Compoment by Id", tags = {"productCategorys", "put"})
    @ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = ComponentResources.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ComponentResponse> update(@PathVariable("id") UUID id, @Valid @RequestBody ComponentRequest request) {
        log.info("Chegada do objeto para ser alterado {}", request);
        var productCategory = componentApiMapper.fromRequest(request);
        ChangeComponent updated = updateCompomentPort.update(id, productCategory);
        if (updated == null) {
            throw new ResourceFoundException("\"Produto não encontroado ao atualizar");
        }

        ComponentResponse productCategoryResponse = componentApiMapper.fromEntity(updated);
        return ResponseEntity.ok(productCategoryResponse);
    }

    @Operation(summary = "Retrieve all Compoment", tags = {"productCategorys", "get", "filter"})
    @ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = ComponentResources.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "204", description = "There are no Associations", content = {
            @Content(schema = @Schema())})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ComponentResponse>> findAll() {
        List<ChangeComponent> productCategoryList = findCompomentsPort.findAll();
        List<ComponentResponse> productCategoryResponse = componentApiMapper.map(productCategoryList);
        return productCategoryResponse.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(productCategoryResponse);
    }

    @Operation(
            summary = "Retrieve a Compoment by Id",
            description = "Get a Compoment object by specifying its id. The response is Association object with id, title, description and published status.",
            tags = {"productCategorys", "get"})
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ComponentResources.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ComponentResponse> findOne(@PathVariable("id") UUID id) {
        ChangeComponent productCategorySaved = findByIdCompomentPort.findById(id);
        if (productCategorySaved == null) {
            throw new ResourceFoundException("Produto não encontrado ao buscar por código");
        }

        ComponentResponse productCategoryResponse = componentApiMapper.fromEntity(productCategorySaved);
        return ResponseEntity.ok(productCategoryResponse);
    }

    @Operation(summary = "Delete a Compoment by Id", tags = {"productCategorytrus", "delete"})
    @ApiResponse(responseCode = "204", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> remove(@PathVariable("id") UUID id) {
        deleteCompomentPort.remove(id);
        return ResponseEntity.noContent().build();
    }
}