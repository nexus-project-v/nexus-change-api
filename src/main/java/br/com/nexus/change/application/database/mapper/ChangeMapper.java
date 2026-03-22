package br.com.nexus.change.application.database.mapper;

import br.com.nexus.change.core.domain.change.Change;
import br.com.nexus.change.infrastructure.entity.change.ChangeEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChangeMapper {

    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "componentId", target = "componentEntity.id")
    @Mapping(source = "environment", target = "environment")
    @Mapping(source = "changeType", target = "changeType")
    @Mapping(source = "changeStatus", target = "changeStatus")
    @Mapping(source = "requestBy", target = "requestBy")
    ChangeEntity fromModelTpEntity(Change change);

    @InheritInverseConfiguration
    @Mapping(target = "id", source = "id")
    Change fromEntityToModel(ChangeEntity changeEntity);

    List<Change> map(List<ChangeEntity> changeEntities);
}
