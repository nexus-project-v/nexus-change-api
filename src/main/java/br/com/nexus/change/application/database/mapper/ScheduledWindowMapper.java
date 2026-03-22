package br.com.nexus.change.application.database.mapper;

import br.com.nexus.change.core.domain.schedule.ScheduledWindow;
import br.com.nexus.change.infrastructure.entity.scheduled.ScheduledWindowEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScheduledWindowMapper {

    @Mapping(source = "responsible", target = "responsible")
    @Mapping(source = "start", target = "start")
    @Mapping(source = "end", target = "end")
    @Mapping(source = "changeId", target = "changeEntity.id")
    ScheduledWindowEntity fromModelTpEntity(ScheduledWindow scheduledWindow);

    @InheritInverseConfiguration
    @Mapping(target = "id", source = "id")
    ScheduledWindow fromEntityToModel(ScheduledWindowEntity scheduledWindowEntity);

    List<ScheduledWindow> map(List<ScheduledWindowEntity> scheduledWindowEntity);
}
