package br.com.nexus.change.core.ports.in.scheduled;

import java.util.UUID;

public interface DeleteScheduledWindowPort {
    boolean remove(UUID id);
}
