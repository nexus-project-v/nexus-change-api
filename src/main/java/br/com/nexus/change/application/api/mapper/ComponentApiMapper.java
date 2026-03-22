package br.com.nexus.change.application.api.mapper;

import br.com.nexus.change.application.api.dto.request.ComponentRequest;
import br.com.nexus.change.application.api.dto.response.ComponentResponse;
import br.com.nexus.change.core.domain.component.ChangeComponent;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ComponentApiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "version", target = "version")
    ChangeComponent fromRequest(ComponentRequest request);

    @InheritInverseConfiguration
    @Mapping(target = "id", source = "id")
    ComponentResponse fromEntity(ChangeComponent changeComponent);

    List<ComponentResponse> map(List<ChangeComponent> changeComponents);
}
