package br.com.nexus.change.core.ports.in.change;

import br.com.nexus.change.core.domain.change.Change;

import java.util.UUID;

public interface UpdateChangePort {
    Change update(UUID id, Change change);
    Change updateStatusById(UUID transactionId, String status);
}
