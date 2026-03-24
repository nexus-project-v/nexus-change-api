package br.com.nexus.change.core.service;

import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.core.domain.change.Change;
import br.com.nexus.change.core.domain.change.ChangeLog;
import br.com.nexus.change.core.ports.in.change.*;
import br.com.nexus.change.core.ports.out.ChangeLogRepositoryPort;
import br.com.nexus.change.core.ports.out.ChangeRepositoryPort;
import br.com.nexus.change.infrastructure.entity.change.ChangeStatus;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ChangeService implements CreateChangePort, UpdateChangePort, FindByIdChangePort, FindChangesPort, DeleteChangePort {
    private final ChangeRepositoryPort changeRepositoryPort;
    private final ChangeLogRepositoryPort changeLogRepositoryPort;

    @Autowired
    public ChangeService(ChangeRepositoryPort changeRepositoryPort, ChangeLogRepositoryPort changeLogRepositoryPort) {
        this.changeRepositoryPort = changeRepositoryPort;
        this.changeLogRepositoryPort = changeLogRepositoryPort;
    }

    @Override
    public Change save(Change change) {
        log.info("operation=change.save stage=start traceId={} componentId={} requestBy={}",
                traceId(),
                componentId(change),
                requestBy(change));

        if (change.getChangeStatus() == null || change.getChangeStatus().isEmpty()) {
            change.setChangeStatus(ChangeStatus.DRAFT.name());
        }

        Change saved = changeRepositoryPort.save(change);
        if (saved != null) {
            createChangeLog(saved.getId(), saved.getChangeStatus());
            log.info("operation=change.save stage=success traceId={} changeId={} changeStatus={}",
                    traceId(),
                    saved.getId(),
                    saved.getChangeStatus());
        } else {
            log.warn("operation=change.save stage=finish traceId={} result=null", traceId());
        }

        return saved;
    }

    @Override
    public Change update(UUID id, Change change) {
        log.info("operation=change.update stage=start traceId={} changeId={} componentId={}",
                traceId(),
                id,
                componentId(change));

        Change resultById = findById(id);
        if (resultById != null) {
            resultById.update(id, change);

            Change updated = changeRepositoryPort.save(resultById);
            if (updated != null) {
                log.info("operation=change.update stage=success traceId={} changeId={} changeStatus={}",
                        traceId(),
                        updated.getId(),
                        updated.getChangeStatus());
            } else {
                log.warn("operation=change.update stage=finish traceId={} changeId={} result=null",
                        traceId(),
                        id);
            }

            return updated;
        }

        log.warn("operation=change.update stage=not_found traceId={} changeId={}", traceId(), id);

        return null;
    }

    @Override
    public Change updateStatusById(UUID transactionId, String status) {
        return null;
    }

    @Override
    public Change updateStatus(UUID changeId, String changeStatus) {
        log.info("operation=change.update-status stage=start traceId={} changeId={} changeStatus={}",
                traceId(),
                changeId,
                changeStatus);

        Change changeById = findById(changeId);
        if (changeById == null) {
            log.warn("operation=change.update-status stage=not_found traceId={} changeId={}", traceId(), changeId);
            throw new ResourceFoundException("Change not found");
        }

        changeById.setChangeStatus(changeStatus);
        Change updated = changeRepositoryPort.save(changeById);
        if (updated != null) {
            createChangeLog(changeId, changeStatus);
            log.info("operation=change.update-status stage=success traceId={} changeId={} changeStatus={}",
                    traceId(),
                    changeId,
                    changeStatus);
            return updated;
        }

        log.warn("operation=change.update-status stage=finish traceId={} changeId={} result=null",
                traceId(),
                changeId);
        return null;
    }

    @Override
    public Change findById(UUID id) {
        log.debug("operation=change.find-by-id traceId={} changeId={}", traceId(), id);
        return changeRepositoryPort.findById(id);
    }

    @Override
    public List<Change> findAll() {
        log.debug("operation=change.find-all traceId={}", traceId());
        return changeRepositoryPort.findAll();
    }

    @Override
    public boolean remove(UUID id) {
        log.info("operation=change.remove stage=start traceId={} changeId={}", traceId(), id);

        Change changeById = findById(id);
        if (changeById == null) {
            log.warn("operation=change.remove stage=not_found traceId={} changeId={}", traceId(), id);
            throw new ResourceFoundException("Change not found");
        }

        changeRepositoryPort.remove(id);
        log.info("operation=change.remove stage=success traceId={} changeId={}", traceId(), id);
        return Boolean.TRUE;
    }

    private void createChangeLog(UUID changeId, String changeStatus) {
        log.debug("operation=change-log.save stage=start traceId={} changeId={} changeStatus={}",
                traceId(),
                changeId,
                changeStatus);

        ChangeLog changeLog = ChangeLog.builder()
                .changeId(changeId)
                .changeStatus(changeStatus)
                .build();
        changeLogRepositoryPort.save(changeLog);

        log.debug("operation=change-log.save stage=success traceId={} changeId={} changeStatus={}",
                traceId(),
                changeId,
                changeStatus);
    }

    private UUID componentId(Change change) {
        return change != null ? change.getComponentId() : null;
    }

    private String requestBy(Change change) {
        return change != null ? change.getRequestBy() : null;
    }

    private String traceId() {
        String traceId = MDC.get("traceId");
        return isBlank(traceId) ? "N/A" : traceId;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
