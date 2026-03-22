package br.com.nexus.change.core.domain.component;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Schema(description = "Component", requiredProperties = {"id, name"})
@Tag(name = "Component", description = "Model")
public class ChangeComponent implements Serializable {

    @Schema(description = "Unique identifier of the component Change.",
            example = "1")
    private UUID id;

    @Schema(description = "name of component of the Change.",
            example = "infra-cluster-eks")
    @NotNull(message = "o campo \"name\" é obrigario")
    @Size(min = 3, max = 255)
    private String name;

    @Schema(description = "version of component of the Change.",
            example = "1.29")
    private String version;

    public void update(UUID id, ChangeComponent changeComponent) {
        this.id = id;
        this.name = changeComponent.getName();
        this.version = changeComponent.getVersion();
    }
}
