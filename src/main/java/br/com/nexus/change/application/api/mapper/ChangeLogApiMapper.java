package br.com.nexus.change.application.api.mapper;

import br.com.nexus.change.application.api.dto.response.ChangeLogResponse;
import br.com.nexus.change.core.domain.change.ChangeLog;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChangeLogApiMapper {

    @InheritInverseConfiguration
    @Mapping(target = "id", source = "id")
    @Mapping(source = "changeStatus", target = "changeStatus")
    @Mapping(source = "changeId", target = "changeId")
    @Mapping(source = "createdDate", target = "createdDate")
    ChangeLogResponse fromEntity(ChangeLog changeLog);

    List<ChangeLogResponse> map(List<ChangeLog> changeLogs);

}
