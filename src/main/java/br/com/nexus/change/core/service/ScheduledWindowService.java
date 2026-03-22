package br.com.nexus.change.core.service;

import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.core.domain.schedule.ScheduledWindow;
import br.com.nexus.change.core.ports.in.scheduled.*;
import br.com.nexus.change.core.ports.out.ScheduledWindowRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ScheduledWindowService implements CreateScheduledWindowPort, UpdateScheduledWindowPort, FindByIdScheduledWindowPort, FindScheduledWindowsPort, DeleteScheduledWindowPort {

    private final ScheduledWindowRepositoryPort scheduledWindowRepositoryPort;

    @Autowired
    public ScheduledWindowService(ScheduledWindowRepositoryPort scheduledWindowRepositoryPort) {
        this.scheduledWindowRepositoryPort = scheduledWindowRepositoryPort;
    }

    @Override
    public ScheduledWindow save(ScheduledWindow product) {
        return scheduledWindowRepositoryPort.save(product);
    }

    @Override
    public ScheduledWindow update(UUID id, ScheduledWindow product) {
        ScheduledWindow resultById = findById(id);
        if (resultById != null) {
            resultById.update(id, product);

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
}
