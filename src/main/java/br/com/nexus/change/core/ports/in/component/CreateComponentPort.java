package br.com.nexus.change.core.ports.in.component;

import br.com.nexus.change.core.domain.component.ChangeComponent;

public interface CreateComponentPort {
    ChangeComponent save(ChangeComponent changeComponent);
}
