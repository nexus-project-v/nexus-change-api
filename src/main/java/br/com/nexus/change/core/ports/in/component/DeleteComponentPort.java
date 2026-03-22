package br.com.nexus.change.core.ports.in.component;

import java.util.UUID;

public interface DeleteComponentPort {
    boolean remove(UUID id);
}
