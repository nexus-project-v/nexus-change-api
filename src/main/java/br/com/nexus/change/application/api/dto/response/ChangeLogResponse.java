package br.com.nexus.change.application.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "ChangeResponse", requiredProperties = {"id, title, description, component, environment, changeType, requestBy"})
@Tag(name = "ChangeResponse", description = "Model")
public class ChangeLogResponse implements Serializable {

    @Schema(description = "Unique identifier of the Change.",
            example = "1")
    private UUID id;

    @Schema(description = "ChangeStatus of Change")
    private String changeStatus;

    @Schema(description = "Restaurant of the User.",
            example = "1", ref = "TransactionStatus")
    @NotNull
    private UUID changeId;

    private LocalDateTime createdDate;
}

