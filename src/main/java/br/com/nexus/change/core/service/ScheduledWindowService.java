package br.com.nexus.change.core.service;

import br.com.nexus.change.application.event.dto.ChangePreparedPayload;
import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.core.domain.change.Change;
import br.com.nexus.change.core.domain.change.ChangeLog;
import br.com.nexus.change.core.domain.component.ChangeComponent;
import br.com.nexus.change.core.domain.schedule.ScheduledWindow;
import br.com.nexus.change.core.ports.in.scheduled.*;
import br.com.nexus.change.core.ports.out.ChangeLogRepositoryPort;
import br.com.nexus.change.core.ports.out.ChangeRepositoryPort;
import br.com.nexus.change.core.ports.out.ScheduledWindowRepositoryPort;
import br.com.nexus.change.core.ports.out.event.ChangeEventPublisher;
import br.com.nexus.change.infrastructure.entity.change.ChangeStatus;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ScheduledWindowService implements CreateScheduledWindowPort, UpdateScheduledWindowPort, FindByIdScheduledWindowPort, FindScheduledWindowsPort, DeleteScheduledWindowPort {

    private final ScheduledWindowRepositoryPort scheduledWindowRepositoryPort;
    private final ChangeRepositoryPort changeRepositoryPort;
    private final ChangeLogRepositoryPort changeLogRepositoryPort;
    private final ComponentService componentService;
    private final ChangeEventPublisher changeEventPublisher;

    @Autowired
    public ScheduledWindowService(ScheduledWindowRepositoryPort scheduledWindowRepositoryPort, ChangeRepositoryPort changeRepositoryPort, ChangeLogRepositoryPort changeLogRepositoryPort, ComponentService componentService, ChangeEventPublisher changeEventPublisher) {
        this.scheduledWindowRepositoryPort = scheduledWindowRepositoryPort;
        this.changeRepositoryPort = changeRepositoryPort;
        this.changeLogRepositoryPort = changeLogRepositoryPort;
        this.componentService = componentService;
        this.changeEventPublisher = changeEventPublisher;
    }

    @Override
    public ScheduledWindow save(ScheduledWindow scheduledWindow) {
        log.info("operation=scheduled-window.save stage=start traceId={} scheduledWindowId={} changeId={} responsible={} start={} end={}",
                traceId(),
                scheduledWindowId(scheduledWindow),
                changeId(scheduledWindow),
                responsible(scheduledWindow),
                start(scheduledWindow),
                end(scheduledWindow));

        UUID changeId = scheduledWindow.getChangeId();
        Change changeById = changeRepositoryPort.findById(changeId);
        if (changeById == null) {
            log.warn("operation=scheduled-window.save stage=change_not_found traceId={} changeId={} scheduledWindowId={}",
                    traceId(),
                    changeId,
                    scheduledWindowId(scheduledWindow));
        }

        if (changeById != null) {
            changeById.setChangeStatus(ChangeStatus.CREATED.name());
            log.info("operation=scheduled-window.save stage=change_status_update traceId={} changeId={} changeStatus={}",
                    traceId(),
                    changeById.getId(),
                    changeById.getChangeStatus());

            Change updated = changeRepositoryPort.update(changeById.getId(), changeById);
            if (updated != null) {
                log.info("operation=scheduled-window.save stage=change_updated traceId={} changeId={} changeStatus={}",
                        traceId(),
                        updated.getId(),
                        updated.getChangeStatus());

                createChangeLog(updated.getId(), updated.getChangeStatus());

                ChangeComponent component = componentService.findById(changeById.getComponentId());
                if (component != null) {
                    log.info("operation=scheduled-window.save stage=component_resolved traceId={} changeId={} componentId={} componentName={} componentVersion={}",
                            traceId(),
                            changeById.getId(),
                            component.getId(),
                            component.getName(),
                            component.getVersion());

                    ChangePreparedPayload payload = ChangePreparedPayload.builder()
                            .changeId(changeById.getId())
                            .component(component.getName())
                            .componentVersion(component.getVersion())
                            .environment(changeById.getEnvironment())
                            .changeType(changeById.getChangeType())
                            .changeStatus(changeById.getChangeStatus())
                            .build();

                    log.info("operation=scheduled-window.save stage=event_publish traceId={} changeId={} eventType=CHANGE_PREPARED",
                            traceId(),
                            changeById.getId());
                    changeEventPublisher.publish(payload);
                    log.info("operation=scheduled-window.save stage=event_published traceId={} changeId={} eventType=CHANGE_PREPARED",
                            traceId(),
                            changeById.getId());
                } else {
                    log.warn("operation=scheduled-window.save stage=component_not_found traceId={} changeId={} componentId={}",
                            traceId(),
                            changeById.getId(),
                            changeById.getComponentId());
                }
            } else {
                log.warn("operation=scheduled-window.save stage=change_update_failed traceId={} changeId={} result=null",
                        traceId(),
                        changeById.getId());
            }
        }

        ScheduledWindow saved = scheduledWindowRepositoryPort.save(scheduledWindow);
        if (saved != null) {
            log.info("operation=scheduled-window.save stage=success traceId={} scheduledWindowId={} changeId={} responsible={} start={} end={}",
                    traceId(),
                    saved.getId(),
                    saved.getChangeId(),
                    saved.getResponsible(),
                    saved.getStart(),
                    saved.getEnd());
        } else {
            log.warn("operation=scheduled-window.save stage=finish traceId={} result=null changeId={}",
                    traceId(),
                    changeId);
        }

        return saved;
    }

    @Override
    public ScheduledWindow update(UUID id, ScheduledWindow scheduledWindow) {
        log.info("operation=scheduled-window.update stage=start traceId={} scheduledWindowId={} changeId={} responsible={} start={} end={}",
                traceId(),
                id,
                changeId(scheduledWindow),
                responsible(scheduledWindow),
                start(scheduledWindow),
                end(scheduledWindow));

        ScheduledWindow resultById = findById(id);
        if (resultById != null) {
            resultById.update(id, scheduledWindow);

            ScheduledWindow updated = scheduledWindowRepositoryPort.save(resultById);
            if (updated != null) {
                log.info("operation=scheduled-window.update stage=success traceId={} scheduledWindowId={} changeId={} responsible={} start={} end={}",
                        traceId(),
                        updated.getId(),
                        updated.getChangeId(),
                        updated.getResponsible(),
                        updated.getStart(),
                        updated.getEnd());
            } else {
                log.warn("operation=scheduled-window.update stage=finish traceId={} scheduledWindowId={} result=null",
                        traceId(),
                        id);
            }

            return updated;
        }

        log.warn("operation=scheduled-window.update stage=not_found traceId={} scheduledWindowId={}", traceId(), id);

        return null;
    }

    @Override
    public ScheduledWindow findById(UUID id) {
        log.debug("operation=scheduled-window.find-by-id stage=start traceId={} scheduledWindowId={}", traceId(), id);

        ScheduledWindow scheduledWindow = scheduledWindowRepositoryPort.findById(id);
        if (scheduledWindow != null) {
            log.debug("operation=scheduled-window.find-by-id stage=success traceId={} scheduledWindowId={} changeId={} responsible={} start={} end={}",
                    traceId(),
                    scheduledWindow.getId(),
                    scheduledWindow.getChangeId(),
                    scheduledWindow.getResponsible(),
                    scheduledWindow.getStart(),
                    scheduledWindow.getEnd());
        } else {
            log.warn("operation=scheduled-window.find-by-id stage=not_found traceId={} scheduledWindowId={}", traceId(), id);
        }

        return scheduledWindow;
    }

    @Override
    public List<ScheduledWindow> findAll() {
        log.debug("operation=scheduled-window.find-all stage=start traceId={}", traceId());

        List<ScheduledWindow> scheduledWindows = scheduledWindowRepositoryPort.findAll();
        int resultCount = scheduledWindows != null ? scheduledWindows.size() : 0;

        log.debug("operation=scheduled-window.find-all stage=success traceId={} resultCount={}", traceId(), resultCount);
        return scheduledWindows;
    }

    @Override
    public boolean remove(UUID id) {
        try {
            log.info("operation=scheduled-window.remove stage=start traceId={} scheduledWindowId={}", traceId(), id);

            ScheduledWindow scheduledWindowById = findById(id);
            if (scheduledWindowById == null) {
                log.warn("operation=scheduled-window.remove stage=not_found traceId={} scheduledWindowId={}", traceId(), id);
                throw new ResourceFoundException("Scheduled window not found");
            }

            scheduledWindowRepositoryPort.remove(id);
            log.info("operation=scheduled-window.remove stage=success traceId={} scheduledWindowId={}", traceId(), id);
            return Boolean.TRUE;
        } catch (ResourceFoundException e) {
            log.warn("operation=scheduled-window.remove stage=error traceId={} scheduledWindowId={} message={}",
                    traceId(),
                    id,
                    e.getMessage());
            return Boolean.FALSE;
        }
    }

    private void createChangeLog(UUID changeId, String changeStatus) {
        log.debug("operation=scheduled-window.change-log.save stage=start traceId={} changeId={} changeStatus={}",
                traceId(),
                changeId,
                changeStatus);

        ChangeLog build = ChangeLog.builder()
                .changeId(changeId)
                .changeStatus(changeStatus)
                .build();
        changeLogRepositoryPort.save(build);

        log.debug("operation=scheduled-window.change-log.save stage=success traceId={} changeId={} changeStatus={}",
                traceId(),
                changeId,
                changeStatus);
    }

    private UUID scheduledWindowId(ScheduledWindow scheduledWindow) {
        return scheduledWindow != null ? scheduledWindow.getId() : null;
    }

    private UUID changeId(ScheduledWindow scheduledWindow) {
        return scheduledWindow != null ? scheduledWindow.getChangeId() : null;
    }

    private String responsible(ScheduledWindow scheduledWindow) {
        return scheduledWindow != null ? scheduledWindow.getResponsible() : null;
    }

    private Object start(ScheduledWindow scheduledWindow) {
        return scheduledWindow != null ? scheduledWindow.getStart() : null;
    }

    private Object end(ScheduledWindow scheduledWindow) {
        return scheduledWindow != null ? scheduledWindow.getEnd() : null;
    }

    private String traceId() {
        String traceId = MDC.get("traceId");
        return traceId == null || traceId.trim().isEmpty() ? "N/A" : traceId;
    }
}
