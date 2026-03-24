package br.com.nexus.change.application.api.resources;

import br.com.nexus.change.application.api.dto.response.ChangeStatusResponse;
import br.com.nexus.change.core.service.ChangeStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/change-status")
@CrossOrigin(
        origins = "*",
        allowedHeaders = "*",
        methods = {RequestMethod.OPTIONS, RequestMethod.PATCH, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        maxAge = 3600
)
public class ChangeStatusResources {

    private final ChangeStatusService changeStatusService;

    @Autowired
    public ChangeStatusResources(ChangeStatusService changeStatusService) {
        this.changeStatusService = changeStatusService;
    }

    @Operation(
            summary = "Retrieve status details of a Change by Id",
            description = "Get current status, updated timestamp, timeline and technical logs for a change.",
            tags = {"changes", "status", "get"}
    )
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ChangeStatusResponse.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    @GetMapping("/{changeId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ChangeStatusResponse> getStatus(@PathVariable("changeId") UUID changeId) {
        log.info("Consultando status da change {}", changeId);
        ChangeStatusResponse response = changeStatusService.getStatus(changeId);
        return ResponseEntity.ok(response);
    }
}

