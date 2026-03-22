package br.com.nexus.change.application.database.repository;

import br.com.nexus.change.application.database.mapper.ScheduledWindowMapper;
import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.commons.exception.ResourceNotRemoveException;
import br.com.nexus.change.core.domain.schedule.ScheduledWindow;
import br.com.nexus.change.core.ports.out.ScheduledWindowRepositoryPort;
import br.com.nexus.change.infrastructure.entity.scheduled.ScheduledWindowEntity;
import br.com.nexus.change.infrastructure.repository.ScheduledWindowRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class ScheduledWindowRepositoryAdapter implements ScheduledWindowRepositoryPort {

    private final ScheduledWindowRepository scheduledWindowRepository;
    private final ScheduledWindowMapper scheduledWindowMapper;

    @Autowired
    public ScheduledWindowRepositoryAdapter(ScheduledWindowRepository scheduledWindowRepository, ScheduledWindowMapper scheduledWindowMapper) {
        this.scheduledWindowRepository = scheduledWindowRepository;
        this.scheduledWindowMapper = scheduledWindowMapper;
    }

    @Override
    public ScheduledWindow save(ScheduledWindow scheduledWindow) {
        try {
            ScheduledWindowEntity productCategoryEntity = scheduledWindowMapper.fromModelTpEntity(scheduledWindow);
            ScheduledWindowEntity saved = scheduledWindowRepository.save(productCategoryEntity);
            validateSavedEntity(saved);
            return scheduledWindowMapper.fromEntityToModel(saved);
        } catch (ResourceFoundException e) {
            log.error("Erro ao salvar produto: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean remove(UUID id) {
        try {
            scheduledWindowRepository.deleteById(id);
            return Boolean.TRUE;
        } catch (ResourceNotRemoveException e) {
            return Boolean.FALSE;
        }
    }

    @Override
    public ScheduledWindow findById(UUID id) {
        Optional<ScheduledWindowEntity> buScheduledWindow = scheduledWindowRepository.findById(id);
        return buScheduledWindow.map(scheduledWindowMapper::fromEntityToModel).orElse(null);
    }

    @Override
    public List<ScheduledWindow> findAll() {
        List<ScheduledWindowEntity> all = scheduledWindowRepository.findAll();
        return scheduledWindowMapper.map(all);
    }

    @Override
    public ScheduledWindow update(UUID id, ScheduledWindow productCategory) {
        Optional<ScheduledWindowEntity> resultById = scheduledWindowRepository.findById(id);
        if (resultById.isPresent()) {
            ScheduledWindowEntity productCategoryToChange = resultById.get();
            productCategoryToChange.update(id, productCategoryToChange);

            return scheduledWindowMapper.fromEntityToModel(scheduledWindowRepository.save(productCategoryToChange));
        }
        return null;
    }

    private void validateSavedEntity(ScheduledWindowEntity saved) {
        if (saved == null) {
            throw new ResourceFoundException("Erro ao salvar produto no repositorio: entidade salva é nula");
        }

        if (saved.getResponsible() == null) {
            throw new ResourceFoundException("Erro ao salvar produto no repositorio: nome do produto é nulo");
        }
    }
}
