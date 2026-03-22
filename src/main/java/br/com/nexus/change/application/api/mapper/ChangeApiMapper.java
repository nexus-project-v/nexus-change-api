package br.com.nexus.change.application.api.mapper;

import br.com.nexus.change.application.api.dto.request.ChangeRequest;
import br.com.nexus.change.application.api.dto.response.ChangeResponse;
import br.com.nexus.change.core.domain.change.Change;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChangeApiMapper {

    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "componentId", target = "componentId")
    @Mapping(source = "environment", target = "environment")
    @Mapping(source = "changeType", target = "changeType")
    @Mapping(source = "requestBy", target = "requestBy")
    Change fromRequest(ChangeRequest request);

    @InheritInverseConfiguration
    @Mapping(target = "id", source = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "componentId", target = "componentId")
    @Mapping(source = "environment", target = "environment")
    @Mapping(source = "changeType", target = "changeType")
    @Mapping(source = "requestBy", target = "requestBy")
    ChangeResponse fromEntity(Change change);

    List<ChangeResponse> map(List<Change> changes);

}
