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

    private final ComponentRepositoryPort transactionRepository;
    private final ComponentRepositoryPort transactionStatusRepository;

    @Autowired
    public ComponentService(ComponentRepositoryPort transactionRepository, ComponentRepositoryPort transactionStatusRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionStatusRepository = transactionStatusRepository;
    }

    @Override
    public ChangeComponent save(ChangeComponent transactionStatus) {
        return transactionStatusRepository.save(transactionStatus);
    }

    @Override
    public ChangeComponent update(UUID id, ChangeComponent transactionStatus) {
        ChangeComponent resultById = findById(id);
        if (resultById != null) {
            resultById.update(id, transactionStatus);

            return transactionStatusRepository.save(resultById);
        }

        return null;
    }

    @Override
    public ChangeComponent findById(UUID id) {
        return transactionStatusRepository.findById(id);
    }

    @Override
    public List<ChangeComponent> findAll() {
        try {
            return transactionStatusRepository.findAll();
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

            transactionRepository.remove(id);
            return Boolean.TRUE;
        } catch (ResourceFoundException e) {
            log.error("Erro ao remover produto: {}", e.getMessage());
            return Boolean.FALSE;
        }
    }
}
