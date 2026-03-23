package br.com.nexus.change.core.ports.out;

import br.com.nexus.change.core.domain.change.Change;
import br.com.nexus.change.core.domain.change.ChangeLog;

import java.util.List;
import java.util.UUID;

public interface ChangeLogRepositoryPort {
    ChangeLog save(ChangeLog changeLog);

    Change findByChangeId(UUID changeId);
}
