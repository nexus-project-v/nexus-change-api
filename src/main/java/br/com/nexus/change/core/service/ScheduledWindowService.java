package br.com.nexus.change.core.service;

import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.core.domain.change.Change;
import br.com.nexus.change.core.domain.change.ChangeLog;
import br.com.nexus.change.core.domain.schedule.ScheduledWindow;
import br.com.nexus.change.core.ports.in.scheduled.*;
import br.com.nexus.change.core.ports.out.ChangeLogRepositoryPort;
import br.com.nexus.change.core.ports.out.ChangeRepositoryPort;
import br.com.nexus.change.core.ports.out.ScheduledWindowRepositoryPort;
import br.com.nexus.change.infrastructure.entity.change.ChangeStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ScheduledWindowService implements CreateScheduledWindowPort, UpdateScheduledWindowPort, FindByIdScheduledWindowPort, FindScheduledWindowsPort, DeleteScheduledWindowPort {

    private final ScheduledWindowRepositoryPort scheduledWindowRepositoryPort;
    private final ChangeRepositoryPort changeRepositoryPort;
    private final ChangeLogRepositoryPort changeLogRepositoryPort;

    @Autowired
    public ScheduledWindowService(ScheduledWindowRepositoryPort scheduledWindowRepositoryPort, ChangeRepositoryPort changeRepositoryPort, ChangeLogRepositoryPort changeLogRepositoryPort) {
        this.scheduledWindowRepositoryPort = scheduledWindowRepositoryPort;
        this.changeRepositoryPort = changeRepositoryPort;
        this.changeLogRepositoryPort = changeLogRepositoryPort;
    }

    @Override
    public ScheduledWindow save(ScheduledWindow scheduledWindow) {
        UUID changeId = scheduledWindow.getChangeId();
        Change changeById = changeRepositoryPort.findById(changeId);
        if (changeById != null) {
            changeById.setChangeStatus(ChangeStatus.CREATED.name());
            Change updated = changeRepositoryPort.update(changeById.getId(), changeById);
            createChangeLog(updated);
        }
        return scheduledWindowRepositoryPort.save(scheduledWindow);
    }

    @Override
    public ScheduledWindow update(UUID id, ScheduledWindow scheduledWindow) {
        ScheduledWindow resultById = findById(id);
        if (resultById != null) {
            resultById.update(id, scheduledWindow);

            return scheduledWindowRepositoryPort.save(resultById);
        }

        return null;
    }

    @Override
    public ScheduledWindow findById(UUID id) {
        return scheduledWindowRepositoryPort.findById(id);
    }

    @Override
    public List<ScheduledWindow> findAll() {
       return scheduledWindowRepositoryPort.findAll();
    }

    @Override
    public boolean remove(UUID id) {
        try {
            ScheduledWindow productCategoryById = findById(id);
            if (productCategoryById == null) {
                throw new ResourceFoundException("Transaction Category not found");
            }

            scheduledWindowRepositoryPort.remove(id);
            return Boolean.TRUE;
        } catch (ResourceFoundException e) {
            log.error("Erro ao remover produto: {}", e.getMessage());
            return Boolean.FALSE;
        }
    }

    private void createChangeLog(Change saved) {
        ChangeLog build = ChangeLog.builder()
                .changeId(saved.getId())
                .changeStatus(saved.getChangeStatus())
                .build();
        changeLogRepositoryPort.save(build);
    }
}
