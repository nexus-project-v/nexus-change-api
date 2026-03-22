package br.com.nexus.change.application.api.mapper;

import br.com.nexus.change.application.api.dto.request.ScheduledWindowRequest;
import br.com.nexus.change.application.api.dto.response.ScheduledWindowResponse;
import br.com.nexus.change.core.domain.schedule.ScheduledWindow;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScheduleWindowApiMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "version", target = "version")
    ScheduledWindow fromRequest(ScheduledWindowRequest request);

    @InheritInverseConfiguration
    @Mapping(target = "id", source = "id")
    ScheduledWindowResponse fromEntity(ScheduledWindow scheduledWindow);

    List<ScheduledWindowResponse> map(List<ScheduledWindow> scheduledWindows);
}
