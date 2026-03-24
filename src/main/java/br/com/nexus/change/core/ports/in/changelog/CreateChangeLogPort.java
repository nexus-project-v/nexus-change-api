package br.com.nexus.change.core.ports.in.changelog;

import br.com.nexus.change.core.domain.change.ChangeLog;

public interface CreateChangeLogPort {
    ChangeLog save(ChangeLog changeLog);
}