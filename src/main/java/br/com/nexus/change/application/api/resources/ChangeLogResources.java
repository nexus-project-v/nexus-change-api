package br.com.nexus.change.application.api.resources;

import br.com.nexus.change.application.api.dto.response.ChangeLogResponse;
import br.com.nexus.change.application.api.mapper.ChangeLogApiMapper;
import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.core.domain.change.ChangeLog;
import br.com.nexus.change.core.ports.in.changelog.FindByIdChangeLogPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/change-logs")
@CrossOrigin(
        origins = "*",
        allowedHeaders = "*",
        methods = {RequestMethod.OPTIONS, RequestMethod.PATCH, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        maxAge = 3600
)
public class ChangeLogResources {

     private final FindByIdChangeLogPort findByIdChangeLogPort;
    private final ChangeLogApiMapper changeLogApiMapper;

    @Autowired
    public ChangeLogResources(FindByIdChangeLogPort findByIdChangeLogPort, ChangeLogApiMapper changeLogApiMapper) {
        this.findByIdChangeLogPort = findByIdChangeLogPort;
        this.changeLogApiMapper = changeLogApiMapper;
    }

    @Operation(
            summary = "Retrieve a Change by Id",
            description = "Get a Change object by specifying its id. The response is Association object with id, title, description and published status.",
            tags = {"changelogs", "get"})
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ChangeLogResources.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ChangeLogResponse> findOne(@PathVariable("id") UUID id) {
        ChangeLog changeSaved = findByIdChangeLogPort.findById(id);
        if (changeSaved == null) {
            throw new ResourceFoundException("Changes não encontrado ao buscar por id");
        }

        ChangeLogResponse transactionResponse = changeLogApiMapper.fromEntity(changeSaved);
        return ResponseEntity.ok(transactionResponse);
    }

    @Operation(
            summary = "Retrieve a Change by Id",
            description = "Get a Change object by specifying its id. The response is Association object with id, title, description and published status.",
            tags = {"changelogs", "get"})
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ChangeLogResources.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    @GetMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ChangeLog>> findByStatus(@PathVariable("id") UUID id) {
        List<ChangeLog> changeLogList = findByIdChangeLogPort.findByStatus(id);
        if (changeLogList == null) {
            throw new ResourceFoundException("Changes não encontrado ao buscar por id");
        }

        return ResponseEntity.ok(changeLogList);
    }
}