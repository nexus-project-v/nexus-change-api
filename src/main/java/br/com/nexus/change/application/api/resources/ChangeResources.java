package br.com.nexus.change.application.api.resources;

import br.com.nexus.change.application.api.dto.request.ChangeRequest;
import br.com.nexus.change.application.api.dto.response.ChangeResponse;
import br.com.nexus.change.application.api.mapper.ChangeApiMapper;
import br.com.nexus.change.commons.exception.ClientCodeFoundException;
import br.com.nexus.change.commons.exception.ProductCodeFoundException;
import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.commons.exception.SellerCodeFoundException;
import br.com.nexus.change.commons.util.RestUtils;
import br.com.nexus.change.core.domain.change.Change;
import br.com.nexus.change.core.ports.in.change.*;
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
@RequestMapping("/v1/changes")
@CrossOrigin(origins = "*", allowedHeaders = "Content-Type, Authorization", maxAge = 3600)
public class ChangeResources {

    private final DeleteChangePort deleteChangePort;
    private final FindByIdChangePort findByIdChangePort;
    private final FindChangesPort findChangesPort;
    private final UpdateChangePort updateChangePort;
    private final CreateChangePort createChangePort;
    private final ChangeApiMapper changeApiMapper;

    @Autowired
    public ChangeResources(DeleteChangePort deleteChangePort, FindByIdChangePort findByIdChangePort, FindChangesPort findChangesPort, UpdateChangePort updateChangePort, CreateChangePort createChangePort, ChangeApiMapper changeApiMapper) {
        this.deleteChangePort = deleteChangePort;
        this.findByIdChangePort = findByIdChangePort;
        this.findChangesPort = findChangesPort;
        this.updateChangePort = updateChangePort;
        this.createChangePort = createChangePort;
        this.changeApiMapper = changeApiMapper;
    }

    @Operation(summary = "Create a new Change", tags = {"transactions", "post"})
    @ApiResponse(responseCode = "201", content = {
            @Content(schema = @Schema(implementation = ChangeResources.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ChangeResponse> save(@Valid @RequestBody ChangeRequest request) throws ProductCodeFoundException, ClientCodeFoundException, SellerCodeFoundException {
        log.info("Chegada do objeto para ser salvo {}", request);
        var transaction = changeApiMapper.fromRequest(request);
        Change saved = createChangePort.save(transaction);
        if (saved == null) {
            throw new ResourceFoundException("Produto não encontroado ao cadastrar");
        }
        URI location = RestUtils.getUri(saved.getId());
        return ResponseEntity.created(location).body(changeApiMapper.fromEntity(saved));
    }

    @Operation(summary = "Update a Change by Id", tags = {"transactions", "put"})
    @ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = ChangeResources.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ChangeResponse> update(@PathVariable("id") UUID id, @Valid @RequestBody ChangeRequest request) {
        log.info("Chegada do objeto para ser alterado {}", request);
        var transaction = changeApiMapper.fromRequest(request);
        Change updated = updateChangePort.update(id, transaction);
        if (updated == null) {
            throw new ResourceFoundException("Produto não encontroado ao atualizar");
        }

        ChangeResponse transactionResponse = changeApiMapper.fromEntity(updated);
        return ResponseEntity.ok(transactionResponse);
    }

    @Operation(summary = "Update a Change by Id", tags = {"transactions", "put"})
    @ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = ChangeResources.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    @PutMapping("/{changeId}/status/{status}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ChangeResponse> updateStatus(@PathVariable("changeId") UUID changeId, @PathVariable("status") String status) {
        Change updated = updateChangePort.updateStatusById(changeId, status);
        if (updated == null) {
            throw new ResourceFoundException("Produto não encontroado ao atualizar");
        }

        ChangeResponse transactionResponse = changeApiMapper.fromEntity(updated);
        return ResponseEntity.ok(transactionResponse);
    }

    @Operation(summary = "Retrieve all Change", tags = {"transactions", "get", "filter"})
    @ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = ChangeResources.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "204", description = "There are no Associations", content = {
            @Content(schema = @Schema())})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ChangeResponse>> findAll() {
        List<Change> changeList = findChangesPort.findAll();
        List<ChangeResponse> transactionResponse = changeApiMapper.map(changeList);
        return transactionResponse.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(transactionResponse);
    }

    @Operation(
            summary = "Retrieve a Change by Id",
            description = "Get a Change object by specifying its id. The response is Association object with id, title, description and published status.",
            tags = {"transactions", "get"})
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ChangeResources.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ChangeResponse> findOne(@PathVariable("id") UUID id) {
        Change changeSaved = findByIdChangePort.findById(id);
        if (changeSaved == null) {
            throw new ResourceFoundException("Produto não encontrado ao buscar por id");
        }

        ChangeResponse transactionResponse = changeApiMapper.fromEntity(changeSaved);
        return ResponseEntity.ok(transactionResponse);
    }

    @Operation(summary = "Delete a Change by Id", tags = {"transactiontrus", "delete"})
    @ApiResponse(responseCode = "204", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> remove(@PathVariable("id") UUID id) {
        deleteChangePort.remove(id);
        return ResponseEntity.noContent().build();
    }
}