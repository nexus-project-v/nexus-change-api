package br.com.nexus.change.core.ports.out;

import br.com.nexus.change.core.domain.change.Change;

import java.util.List;
import java.util.UUID;

public interface ChangeRepositoryPort {
    Change save(Change change);

    void remove(UUID id);

    Change findById(UUID id);

    List<Change> findAll();

    Change update(UUID id, Change change);

}
