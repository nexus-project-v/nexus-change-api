package br.com.nexus.change.core.ports.in.scheduled;

import br.com.nexus.change.core.domain.schedule.ScheduledWindow;

public interface CreateScheduledWindowPort {
    ScheduledWindow save(ScheduledWindow scheduledWindow);
}
