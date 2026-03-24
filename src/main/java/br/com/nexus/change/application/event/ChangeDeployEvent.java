package br.com.nexus.change.application.event;

import br.com.nexus.change.application.event.dto.DeployFinish;
import br.com.nexus.change.application.event.dto.EventEnvelope;
import br.com.nexus.change.core.ports.in.change.UpdateChangePort;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class ChangeDeployEvent {

    private final UpdateChangePort updateChangePort;

    public ChangeDeployEvent(UpdateChangePort updateChangePort) {
        this.updateChangePort = updateChangePort;
    }

    @KafkaListener(topics = "deploy-finished.v1", groupId = "deploy-service")
    public void consumer(EventEnvelope<DeployFinish> envelope) {
        String correlationId = envelope.getCorrelationId();

        log.info("operation=deploy-event.consumer stage=start traceId={} correlationId={} eventId={} eventType={} version={} source={} occurredAt={}",
                traceId(),
                correlationId,
                envelope.getEventId(),
                envelope.getEventType(),
                envelope.getVersion(),
                envelope.getSource(),
                envelope.getOccurredAt());

        DeployFinish data = envelope.getData();

        if (data == null) {
            log.warn("operation=deploy-event.consumer stage=empty_payload traceId={} correlationId={} eventId={}",
                    traceId(),
                    correlationId,
                    envelope.getEventId());
            return;
        }

        UUID changeId = data.getChangeId();
        String changeStatus = data.getChangeStatus();

        log.info("operation=deploy-event.consumer stage=processing traceId={} correlationId={} changeId={} changeStatus={}",
                traceId(),
                correlationId,
                changeId,
                changeStatus);

        try {
            updateChangePort.updateStatus(changeId, changeStatus);

            log.info("operation=deploy-event.consumer stage=success traceId={} correlationId={} changeId={} changeStatus={}",
                    traceId(),
                    correlationId,
                    changeId,
                    changeStatus);
        } catch (Exception e) {
            log.error("operation=deploy-event.consumer stage=error traceId={} correlationId={} changeId={} changeStatus={} message={}",
                    traceId(),
                    correlationId,
                    changeId,
                    changeStatus,
                    e.getMessage(),
                    e);
            throw e;
        }
    }

    private String traceId() {
        String traceId = MDC.get("traceId");
        return traceId == null || traceId.trim().isEmpty() ? "N/A" : traceId;
    }
}
