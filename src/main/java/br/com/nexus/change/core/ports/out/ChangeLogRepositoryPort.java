package br.com.nexus.change.core.ports.out;

import br.com.nexus.change.core.domain.change.ChangeLog;

import java.util.List;
import java.util.UUID;

public interface ChangeLogRepositoryPort {
    ChangeLog save(ChangeLog changeLog);

    List<ChangeLog> findByChangeId(UUID changeId);

    ChangeLog findById(UUID id);
}
