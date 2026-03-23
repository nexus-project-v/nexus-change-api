package br.com.nexus.change.core.service;

import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.core.domain.component.ChangeComponent;
import br.com.nexus.change.core.ports.in.component.*;
import br.com.nexus.change.core.ports.out.ComponentRepositoryPort;
import br.com.nexus.change.core.ports.out.ComponentRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ComponentService implements CreateComponentPort, UpdateComponentPort, FindByIdComponentsPort, FindComponentPort, DeleteComponentPort {

    private final ComponentRepositoryPort componentRepositoryPort;

    @Autowired
    public ComponentService(ComponentRepositoryPort componentRepositoryPort) {
        this.componentRepositoryPort = componentRepositoryPort;
    }

    @Override
    public ChangeComponent save(ChangeComponent transactionStatus) {
        return componentRepositoryPort.save(transactionStatus);
    }

    @Override
    public ChangeComponent update(UUID id, ChangeComponent transactionStatus) {
        ChangeComponent resultById = findById(id);
        if (resultById != null) {
            resultById.update(id, transactionStatus);

            return componentRepositoryPort.save(resultById);
        }

        return null;
    }

    @Override
    public ChangeComponent findById(UUID id) {
        return componentRepositoryPort.findById(id);
    }

    @Override
    public List<ChangeComponent> findAll() {
        try {
            return componentRepositoryPort.findAll();
        } catch (Exception e) {
            log.error("Erro ao buscar produtos: {}", e.getMessage());
        }

        return null;
    }

    @Override
    public boolean remove(UUID id) {
        try {
            ChangeComponent transactionById = findById(id);
            if (transactionById == null) {
                throw new ResourceFoundException("Component not found");
            }

            componentRepositoryPort.remove(id);
            return Boolean.TRUE;
        } catch (ResourceFoundException e) {
            log.error("Erro ao remover produto: {}", e.getMessage());
            return Boolean.FALSE;
        }
    }
}
