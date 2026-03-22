package br.com.nexus.change.application.api.dto.request;

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
@Schema(description = "ChangeRequest", requiredProperties = {"id, title, description, component, environment, changeType, requestBy"})
@Tag(name = "ChangeRequest", description = "Model")
public class ChangeRequest implements Serializable {

    @Schema(description = "title of the Change.",
            example = "Atualização cluster Kubernetes")
    @NotNull(message = "o campo \"title\" é obrigario")
    @Size(min = 3, max = 255)
    private String title;

    @Schema(description = "description of the Change.",
            example = "Upgrade do cluster EKS para versão 1.29 com correções de segurança")
    private String description;

    @Schema(description = "Component of Change", ref = "Component")
    private UUID componentId;

    @Schema(description = "Ambiente da Change", example = "PROD")
    private String environment;

    @Schema(description = "ChangeType of Change", ref = "ChangeType")
    private String changeType;

    @Schema(description = "Restaurant of the User.",
            example = "1", ref = "TransactionStatus")
    @NotNull
    private String requestBy;
}

