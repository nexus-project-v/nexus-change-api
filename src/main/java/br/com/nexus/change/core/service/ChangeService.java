package br.com.nexus.change.core.service;

import br.com.nexus.change.application.event.dto.ChangePreparedPayload;
import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.core.domain.change.Change;
import br.com.nexus.change.core.domain.component.ChangeComponent;
import br.com.nexus.change.core.ports.in.change.*;
import br.com.nexus.change.core.ports.out.ChangeRepositoryPort;
import br.com.nexus.change.core.ports.out.event.ChangeEventPublisher;
import br.com.nexus.change.infrastructure.entity.change.ChangeStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ChangeService implements CreateChangePort, UpdateChangePort, FindByIdChangePort, FindChangesPort, DeleteChangePort {

    private final ChangeRepositoryPort changeRepository;
    private final ChangeEventPublisher changeEventPublisher;
    private final ComponentService componentService;

    @Autowired
    public ChangeService(ChangeRepositoryPort changeRepository, ChangeEventPublisher changeEventPublisher, ComponentService componentService) {
        this.changeRepository = changeRepository;
        this.changeEventPublisher = changeEventPublisher;
        this.componentService = componentService;
    }

    @Override
    public Change save(Change change) {
        if (change.getChangeStatus() == null || change.getChangeStatus().isEmpty()) {
            change.setChangeStatus(ChangeStatus.DRAFT.name());
        }
        Change saved = changeRepository.save(change);
        if (saved != null) {
            ChangeComponent byId = componentService.findById(saved.getComponentId());
            ChangePreparedPayload payload = ChangePreparedPayload.builder()
                    .changeId(saved.getId())
                    .component(byId.getName())
                    .componentVersion(byId.getVersion())
                    .environment(saved.getEnvironment())
                    .changeType(saved.getChangeType())
                    .changeStatus(saved.getChangeStatus())
                    .build();
            changeEventPublisher.publish(payload);
            return saved;
        }
        return null;
    }

    @Override
    public Change update(UUID id, Change change) {
        Change resultById = findById(id);
        if (resultById != null) {
            resultById.update(id, change);

            return changeRepository.save(resultById);
        }

        return null;
    }

    @Override
    public Change updateStatusById(UUID transactionId, String status) {
        return null;
    }

    /*@Override
    public Change updateStatus(PaymentProcessedDTO.Payload payload) {
        try {
            String changeCode = payload.getChangeCode();
            String status = payload.getStatus();

            sendProductionByStatus(payload, changeCode);

            Change resultByCode = findByCode(changeCode);
            if (resultByCode != null) {
                resultByCode.setChangeStatus(status);

                Change changeUpdated = changeRepository.update(resultByCode.getId(), resultByCode);
                if (changeUpdated != null) {
                    ChangeStatus changeStatus = buildChangeStatus(payload, changeUpdated);
                    changeStatusRepository.save(changeStatus);
                }

                return changeUpdated;
            }
        } catch (Exception e) {
            log.error("Erro ao atualizar status da transação: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao atualizar status da transação", e);
        }
        return null;
    }*/


    @Override
    public Change findById(UUID id) {
        return changeRepository.findById(id);
    }

    @Override
    public List<Change> findAll() {
        return changeRepository.findAll();
    }

    @Override
    public boolean remove(UUID id) {
        Change changeById = findById(id);
        if (changeById == null) {
            throw new ResourceFoundException("Change not found");
        }

        changeRepository.remove(id);
        return Boolean.TRUE;
    }
}
