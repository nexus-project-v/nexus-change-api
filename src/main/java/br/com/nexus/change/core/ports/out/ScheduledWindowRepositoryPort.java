package br.com.nexus.change.core.ports.out;

import br.com.nexus.change.core.domain.schedule.ScheduledWindow;

import java.util.List;
import java.util.UUID;

public interface ScheduledWindowRepositoryPort {
    ScheduledWindow save(ScheduledWindow scheduledWindow);
    boolean remove(UUID id);
    ScheduledWindow findById(UUID id);
    List<ScheduledWindow> findAll();
    ScheduledWindow update(UUID id, ScheduledWindow scheduledWindow);
}
