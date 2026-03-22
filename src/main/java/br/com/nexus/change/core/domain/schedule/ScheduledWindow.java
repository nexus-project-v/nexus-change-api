package br.com.nexus.change.core.domain.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Schema(description = "ScheduledWindow", requiredProperties = {"id, responsible", "start", "end", "change"})
@Tag(name = "ScheduledWindow", description = "Model")
public class ScheduledWindow implements Serializable {

    @Schema(description = "Unique identifier of the Transaction.",
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

    public void update(UUID id, ScheduledWindow scheduledWindow) {
        this.id = id;
        this.responsible = scheduledWindow.getResponsible();
        this.start = scheduledWindow.getStart();
        this.end = scheduledWindow.getEnd();
        this.changeId = scheduledWindow.getChangeId();
    }
}
