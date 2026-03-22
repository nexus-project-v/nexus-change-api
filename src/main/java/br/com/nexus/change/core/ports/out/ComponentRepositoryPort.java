package br.com.nexus.change.core.ports.out;

import br.com.nexus.change.core.domain.component.ChangeComponent;

import java.util.List;
import java.util.UUID;

public interface ComponentRepositoryPort {
    ChangeComponent save(ChangeComponent changeComponent);
    void remove(UUID id);
    ChangeComponent findById(UUID id);
    List<ChangeComponent> findAll();
    ChangeComponent update(UUID id, ChangeComponent changeComponent);
}
