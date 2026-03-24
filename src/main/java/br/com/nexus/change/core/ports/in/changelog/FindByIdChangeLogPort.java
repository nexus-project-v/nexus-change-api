package br.com.nexus.change.core.ports.in.changelog;

import br.com.nexus.change.core.domain.change.ChangeLog;

import java.util.List;
import java.util.UUID;

public interface FindByIdChangeLogPort {
    List<ChangeLog> findByStatus(UUID id);

    ChangeLog findById(UUID id);
}
