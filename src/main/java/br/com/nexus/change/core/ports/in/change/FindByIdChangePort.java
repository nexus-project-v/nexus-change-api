package br.com.nexus.change.core.ports.in.change;

import br.com.nexus.change.core.domain.change.Change;
import br.com.nexus.change.core.domain.change.ChangeLog;

import java.util.List;
import java.util.UUID;

public interface FindByIdChangePort {
    Change findById(UUID id);
}
