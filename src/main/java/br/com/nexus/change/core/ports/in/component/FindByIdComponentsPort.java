package br.com.nexus.change.core.ports.in.component;

import br.com.nexus.change.core.domain.component.ChangeComponent;

import java.util.UUID;

public interface FindByIdComponentsPort {
    ChangeComponent findById(UUID id);
}
