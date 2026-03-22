package br.com.nexus.change.core.ports.in.component;

import br.com.nexus.change.core.domain.component.ChangeComponent;

import java.util.List;

public interface FindComponentPort {
    List<ChangeComponent> findAll();
}
