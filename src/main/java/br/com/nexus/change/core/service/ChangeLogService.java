package br.com.nexus.change.core.service;

import br.com.nexus.change.core.domain.change.ChangeLog;
import br.com.nexus.change.core.ports.in.changelog.CreateChangeLogPort;
import br.com.nexus.change.core.ports.in.changelog.FindByIdChangeLogPort;
import br.com.nexus.change.core.ports.out.ChangeLogRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ChangeLogService implements CreateChangeLogPort, FindByIdChangeLogPort {

    private final ChangeLogRepositoryPort changeLogRepositoryPort;

    @Autowired
    public ChangeLogService(ChangeLogRepositoryPort changeLogRepositoryPort) {
        this.changeLogRepositoryPort = changeLogRepositoryPort;
    }

    @Override
    public ChangeLog save(ChangeLog changeLog) {
        log.info("operation=change-log.save stage=start traceId={} changeId={} changeStatus={}",
                traceId(),
                changeId(changeLog),
                changeStatus(changeLog));

        ChangeLog saved = changeLogRepositoryPort.save(changeLog);
        if (saved != null) {
            log.info("operation=change-log.save stage=success traceId={} changeLogId={} changeId={} changeStatus={}",
                    traceId(),
                    saved.getId(),
                    saved.getChangeId(),
                    saved.getChangeStatus());
        } else {
            log.warn("operation=change-log.save stage=finish traceId={} result=null changeId={}",
                    traceId(),
                    changeId(changeLog));
        }

        return saved;
    }

    @Override
    public ChangeLog findById(UUID id) {
        log.debug("operation=change-log.find-by-id stage=start traceId={} changeLogId={}", traceId(), id);

        ChangeLog changeLog = changeLogRepositoryPort.findById(id);
        if (changeLog != null) {
            log.debug("operation=change-log.find-by-id stage=success traceId={} changeLogId={} changeId={} changeStatus={}",
                    traceId(),
                    changeLog.getId(),
                    changeLog.getChangeId(),
                    changeLog.getChangeStatus());
        } else {
            log.warn("operation=change-log.find-by-id stage=not_found traceId={} changeLogId={}", traceId(), id);
        }

        return changeLog;
    }

    @Override
    public List<ChangeLog> findByStatus(UUID id) {
        log.debug("operation=change-log.find-by-change-id stage=start traceId={} changeId={}", traceId(), id);

        List<ChangeLog> changeLogs = changeLogRepositoryPort.findByChangeId(id);
        int resultCount = changeLogs != null ? changeLogs.size() : 0;

        log.debug("operation=change-log.find-by-change-id stage=success traceId={} changeId={} resultCount={}",
                traceId(),
                id,
                resultCount);

        return changeLogs;
    }

    private UUID changeId(ChangeLog changeLog) {
        return changeLog != null ? changeLog.getChangeId() : null;
    }

    private String changeStatus(ChangeLog changeLog) {
        return changeLog != null ? changeLog.getChangeStatus() : null;
    }

    private String traceId() {
        String traceId = MDC.get("traceId");
        return traceId == null || traceId.trim().isEmpty() ? "N/A" : traceId;
    }

}
