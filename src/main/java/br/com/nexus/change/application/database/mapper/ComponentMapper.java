package br.com.nexus.change.application.database.mapper;

import br.com.nexus.change.core.domain.component.ChangeComponent;
import br.com.nexus.change.infrastructure.entity.component.ComponentEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ComponentMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "version", target = "version")
    ComponentEntity fromModelTpEntity(ChangeComponent changeComponent);

    @InheritInverseConfiguration
    @Mapping(target = "id", source = "id")
    ChangeComponent fromEntityToModel(ComponentEntity componentEntity);

    List<ChangeComponent> map(List<ComponentEntity> componentEntities);
}
