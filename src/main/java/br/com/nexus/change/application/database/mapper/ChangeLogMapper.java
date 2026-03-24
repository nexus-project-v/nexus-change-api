package br.com.nexus.change.application.database.mapper;

import br.com.nexus.change.core.domain.change.ChangeLog;
import br.com.nexus.change.infrastructure.entity.change.ChangeLogEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChangeLogMapper {

    @Mapping(source = "changeStatus", target = "changeStatus")
    @Mapping(source = "changeId", target = "changeEntity.id")
    ChangeLogEntity fromModelTpEntity(ChangeLog changeLog);

    @InheritInverseConfiguration
    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdDate", source = "createdDate")
    ChangeLog fromEntityToModel(ChangeLogEntity changeLogEntity);

    List<ChangeLog> map(List<ChangeLogEntity> changeLogEntities);
}
