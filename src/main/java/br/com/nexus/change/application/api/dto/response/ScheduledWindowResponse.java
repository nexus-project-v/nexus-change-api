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
@Schema(description = "ScheduledWindowResponse", requiredProperties = {"id", "name"})
@Tag(name = "ScheduledWindowResponse", description = "Model")
public class ScheduledWindowResponse implements Serializable {

    @Schema(description = "Unique identifier of the Driver.",
            example = "1")
    private UUID id;

    @Schema(description = "responsible of the Change.",
            example = "infra-cluster-eks")
    @NotNull(message = "o campo \"responsible\" é obrigario")
    @Size(min = 1, max = 255)
    private String responsible;

    @Schema(description = "start of the Change.",
            example = "Janela de Manutenção")
    @NotNull(message = "o campo \"start\" é obrigario")
    private LocalDateTime start;

    @Schema(description = "end of the Change.",
            example = "Janela de Manutenção")
    @NotNull(message = "o campo \"end\" é obrigario")
    private LocalDateTime end;

    @Schema(description = "Change Id for the Window.",
            example = "Janela de Manutenção")
    @NotNull(message = "o campo \"change\" é obrigario")
    private UUID changeId;
}
