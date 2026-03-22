package br.com.nexus.change.core.ports.in.scheduled;

import br.com.nexus.change.core.domain.schedule.ScheduledWindow;

import java.util.UUID;

public interface UpdateScheduledWindowPort {
    ScheduledWindow update(UUID id, ScheduledWindow scheduledWindow);
}
