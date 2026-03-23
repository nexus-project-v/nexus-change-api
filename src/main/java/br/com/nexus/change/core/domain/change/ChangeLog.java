package br.com.nexus.change.core.domain.change;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "ChangeLog", requiredProperties = {"id, title, description, component, environment, changeType, requestBy"})
@Tag(name = "ChangeLog", description = "Model")
public class ChangeLog implements Serializable {

    @Schema(description = "Unique identifier of the Change.",
            example = "1")
    private UUID id;

    @Schema(description = "ChangeType of Change", ref = "ChangeType")
    private String changeStatus;

    @Schema(description = "Component of Change", ref = "Component")
    private UUID changeId;

    public void update(UUID id, ChangeLog change) {
        this.id = id;
        this.changeStatus = change.getChangeStatus();
        this.changeId = change.getChangeId();
    }
}
