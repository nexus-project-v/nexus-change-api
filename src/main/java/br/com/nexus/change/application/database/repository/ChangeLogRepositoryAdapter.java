package br.com.nexus.change.application.database.repository;

import br.com.nexus.change.application.database.mapper.ChangeLogMapper;
import br.com.nexus.change.application.database.mapper.ChangeMapper;
import br.com.nexus.change.core.domain.change.Change;
import br.com.nexus.change.core.domain.change.ChangeLog;
import br.com.nexus.change.core.ports.out.ChangeLogRepositoryPort;
import br.com.nexus.change.core.ports.out.ChangeRepositoryPort;
import br.com.nexus.change.infrastructure.entity.change.ChangeEntity;
import br.com.nexus.change.infrastructure.entity.change.ChangeLogEntity;
import br.com.nexus.change.infrastructure.repository.ChangeLogRepository;
import br.com.nexus.change.infrastructure.repository.ChangeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class ChangeLogRepositoryAdapter implements ChangeLogRepositoryPort {

    private final ChangeLogRepository changeLogRepository;
    private final ChangeLogMapper changeLogMapper;

    @Autowired
    public ChangeLogRepositoryAdapter(ChangeLogRepository changeLogRepository, ChangeLogMapper changeLogMapper) {
        this.changeLogRepository = changeLogRepository;
        this.changeLogMapper = changeLogMapper;
    }

    @Override
    public ChangeLog save(ChangeLog changeLog) {
        ChangeLogEntity changeLogEntity = changeLogMapper.fromModelTpEntity(changeLog);
        if (changeLogEntity != null) {
            ChangeLogEntity saved = changeLogRepository.save(changeLogEntity);
            return changeLogMapper.fromEntityToModel(saved);
        }
        return null;
    }

    @Override
    public List<ChangeLog> findByChangeId(UUID changeId) {
        changeLogRepository.findByChangeEntityId(changeId)
                .forEach(changeLogEntity -> log.info("ChangeLog found: {}", changeLogEntity.getId()));
        return changeLogMapper.map(changeLogRepository.findByChangeEntityId(changeId));
    }

    @Override
    public ChangeLog findById(UUID id) {
        return changeLogRepository.findById(id).map(changeLogMapper::fromEntityToModel).orElse(null);
    }
}
