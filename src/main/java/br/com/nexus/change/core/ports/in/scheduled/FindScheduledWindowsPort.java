package br.com.nexus.change.core.ports.in.scheduled;

import br.com.nexus.change.core.domain.schedule.ScheduledWindow;

import java.util.List;

public interface FindScheduledWindowsPort {
    List<ScheduledWindow> findAll();
}
