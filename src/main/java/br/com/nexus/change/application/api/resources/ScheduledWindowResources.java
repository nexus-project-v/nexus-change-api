package br.com.nexus.change.application.api.resources;

import br.com.nexus.change.application.api.dto.request.ScheduledWindowRequest;
import br.com.nexus.change.application.api.dto.response.ScheduledWindowResponse;
import br.com.nexus.change.application.api.mapper.ScheduleWindowApiMapper;
import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.commons.util.RestUtils;
import br.com.nexus.change.core.domain.schedule.ScheduledWindow;
import br.com.nexus.change.core.ports.in.scheduled.*;
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
@RequestMapping("/v1/scheduleds")
@CrossOrigin(origins = "*", allowedHeaders = "Content-Type, Authorization", maxAge = 3600)
public class ScheduledWindowResources {

    private final CreateScheduledWindowPort createScheduledWindowPort;
    private final DeleteScheduledWindowPort deleteScheduledWindowPort;
    private final FindByIdScheduledWindowPort findByIdScheduledWindowPort;
    private final FindScheduledWindowsPort findScheduledWindowsPort;
    private final UpdateScheduledWindowPort updateScheduledWindowPort;
    private final ScheduleWindowApiMapper scheduleWindowApiMapper;

    @Autowired
    public ScheduledWindowResources(CreateScheduledWindowPort createScheduledWindowPort, DeleteScheduledWindowPort deleteScheduledWindowPort, FindByIdScheduledWindowPort findByIdScheduledWindowPort, FindScheduledWindowsPort findScheduledWindowsPort, UpdateScheduledWindowPort updateScheduledWindowPort, ScheduleWindowApiMapper scheduleWindowApiMapper) {
        this.createScheduledWindowPort = createScheduledWindowPort;
        this.deleteScheduledWindowPort = deleteScheduledWindowPort;
        this.findByIdScheduledWindowPort = findByIdScheduledWindowPort;
        this.findScheduledWindowsPort = findScheduledWindowsPort;
        this.updateScheduledWindowPort = updateScheduledWindowPort;
        this.scheduleWindowApiMapper = scheduleWindowApiMapper;
    }

    @Operation(summary = "Create a new Compoment", tags = {"productCategorys", "post"})
    @ApiResponse(responseCode = "201", content = {
            @Content(schema = @Schema(implementation = ScheduledWindowResources.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ScheduledWindowResponse> save(@Valid @RequestBody ScheduledWindowRequest request) {
        log.info("Chegada do objeto para ser salvo {}", request);
        ScheduledWindow productCategory = scheduleWindowApiMapper.fromRequest(request);
        ScheduledWindow saved = createScheduledWindowPort.save(productCategory);
        if (saved == null) {
            throw new ResourceFoundException("Produto não encontroado ao cadastrar");
        }

        ScheduledWindowResponse productCategoryResponse = scheduleWindowApiMapper.fromEntity(saved);
        URI location = RestUtils.getUri(productCategoryResponse.getId());
        return ResponseEntity.created(location).body(productCategoryResponse);
    }

    @Operation(summary = "Update a Compoment by Id", tags = {"productCategorys", "put"})
    @ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = ScheduledWindowResources.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ScheduledWindowResponse> update(@PathVariable("id") UUID id, @Valid @RequestBody ScheduledWindowRequest request) {
        log.info("Chegada do objeto para ser alterado {}", request);
        var productCategory = scheduleWindowApiMapper.fromRequest(request);
        ScheduledWindow updated = updateScheduledWindowPort.update(id, productCategory);
        if (updated == null) {
            throw new ResourceFoundException("\"Produto não encontroado ao atualizar");
        }

        ScheduledWindowResponse productCategoryResponse = scheduleWindowApiMapper.fromEntity(updated);
        return ResponseEntity.ok(productCategoryResponse);
    }

    @Operation(summary = "Retrieve all Compoment", tags = {"productCategorys", "get", "filter"})
    @ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = ScheduledWindowResources.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "204", description = "There are no Associations", content = {
            @Content(schema = @Schema())})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ScheduledWindowResponse>> findAll() {
        List<ScheduledWindow> productCategoryList = findScheduledWindowsPort.findAll();
        List<ScheduledWindowResponse> productCategoryResponse = scheduleWindowApiMapper.map(productCategoryList);
        return productCategoryResponse.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(productCategoryResponse);
    }

    @Operation(
            summary = "Retrieve a Compoment by Id",
            description = "Get a Compoment object by specifying its id. The response is Association object with id, title, description and published status.",
            tags = {"productCategorys", "get"})
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ScheduledWindowResources.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ScheduledWindowResponse> findOne(@PathVariable("id") UUID id) {
        ScheduledWindow productCategorySaved = findByIdScheduledWindowPort.findById(id);
        if (productCategorySaved == null) {
            throw new ResourceFoundException("Produto não encontrado ao buscar por código");
        }

        ScheduledWindowResponse productCategoryResponse = scheduleWindowApiMapper.fromEntity(productCategorySaved);
        return ResponseEntity.ok(productCategoryResponse);
    }

    @Operation(summary = "Delete a Compoment by Id", tags = {"productCategorytrus", "delete"})
    @ApiResponse(responseCode = "204", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> remove(@PathVariable("id") UUID id) {
        deleteScheduledWindowPort.remove(id);
        return ResponseEntity.noContent().build();
    }
}