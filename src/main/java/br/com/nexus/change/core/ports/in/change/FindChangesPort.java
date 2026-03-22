package br.com.nexus.change.core.ports.in.change;

import br.com.nexus.change.core.domain.change.Change;

import java.util.List;

public interface FindChangesPort {
    List<Change> findAll();
}
