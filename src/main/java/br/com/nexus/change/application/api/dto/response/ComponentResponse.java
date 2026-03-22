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
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "ComponentResponse", requiredProperties = {"id", "name", "description", "price", "pic", "productCategoryId", "restaurantId"})
@Tag(name = "ComponentResponse", description = "Model")
public class ComponentResponse implements Serializable {

    @Schema(description = "Unique identifier of the Driver.",
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
}
