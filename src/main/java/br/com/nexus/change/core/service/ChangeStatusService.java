package br.com.nexus.change.core.service;

import br.com.nexus.change.application.api.dto.response.ChangeStatusResponse;
import br.com.nexus.change.application.api.dto.response.LogEntry;
import br.com.nexus.change.application.api.dto.response.TimelineEvent;
import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.core.domain.change.ChangeLog;
import br.com.nexus.change.core.ports.out.ChangeLogRepositoryPort;
import br.com.nexus.change.infrastructure.repository.ChangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class ChangeStatusService {

    private final ChangeRepository changeRepository;
    private final ChangeLogRepositoryPort changeLogRepositoryPort;

    @Autowired
    public ChangeStatusService(ChangeRepository changeRepository, ChangeLogRepositoryPort changeLogRepositoryPort) {
        this.changeRepository = changeRepository;
        this.changeLogRepositoryPort = changeLogRepositoryPort;
    }

    public ChangeStatusResponse getStatus(UUID changeId) {
        var change = changeRepository.findById(changeId)
                .orElseThrow(() -> new ResourceFoundException("Change não encontrada para o id informado"));

        Instant updatedAt = change.getLastModifiedDate() != null
                ? change.getLastModifiedDate().atZone(ZoneId.systemDefault()).toInstant()
                : change.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant();

        List<ChangeLog> changeLogs = changeLogRepositoryPort.findByChangeId(changeId);
        if (changeLogs == null) {
            changeLogs = List.of();
        }

        List<TimelineEvent> timeline = changeLogs.stream()
                .map(log -> TimelineEvent.builder()
                        .event(log.getChangeStatus())
                        .timestamp(toInstant(log, updatedAt))
                        .build())
                .toList();

        List<LogEntry> technicalLogs = changeLogs.stream()
                .map(log -> LogEntry.builder()
                        .timestamp(toInstant(log, updatedAt))
                        .level(resolveLevel(log.getChangeStatus()))
                        .message(buildMessage(log.getChangeStatus()))
                        .build())
                .toList();

        return ChangeStatusResponse.builder()
                .changeId(changeId)
                .changeStatus(change.getChangeStatus() != null ? change.getChangeStatus().name() : null)
                .updatedAt(updatedAt)
                .timeline(timeline)
                .log_change(technicalLogs)
                .build();
    }

    private Instant toInstant(ChangeLog log, Instant fallback) {
        return log.getCreatedDate() != null
                ? log.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant()
                : fallback;
    }

    private String resolveLevel(String status) {
        if (status == null) {
            return "INFO";
        }
        return switch (status) {
            case "ROLLBACK", "REJECTED" -> "WARN";
            default -> "INFO";
        };
    }

    private String buildMessage(String status) {
        return status == null ? "Status não informado" : "Status alterado para " + status;
    }
}