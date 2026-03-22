package br.com.nexus.change.core.ports.in.change;

import java.util.UUID;

public interface DeleteChangePort {
    boolean remove(UUID id);
}
