package br.com.nexus.change.application.event;

import br.com.nexus.change.application.event.dto.ChangePreparedPayload;
import br.com.nexus.change.application.event.dto.EventEnvelope;
import br.com.nexus.change.core.ports.out.event.ChangeEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
public class ChangePreparedEvent implements ChangeEventPublisher {

    private static final String TOPIC = "orders.ready-for-production.v1";
    private static final String EVENT_TYPE = "ORDER_READY_FOR_PRODUCTION";

    private final KafkaTemplate<String, EventEnvelope<ChangePreparedPayload>> kafkaTemplate;

    public ChangePreparedEvent(KafkaTemplate<String, EventEnvelope<ChangePreparedPayload>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(ChangePreparedPayload payload) {
        String correlationId = payload.getChangeId().toString();

        log.info("operation=change-prepared-event.publish stage=build_envelope traceId={} correlationId={} changeId={} component={} componentVersion={} environment={} changeType={} changeStatus={}",
                traceId(),
                correlationId,
                payload.getChangeId(),
                payload.getComponent(),
                payload.getComponentVersion(),
                payload.getEnvironment(),
                payload.getChangeType(),
                payload.getChangeStatus());

        EventEnvelope<ChangePreparedPayload> envelope =
                EventEnvelope.<ChangePreparedPayload>builder()
                        .eventId(UUID.randomUUID().toString())
                        .eventType(EVENT_TYPE)
                        .version("v1")
                        .occurredAt(Instant.now())
                        .correlationId(correlationId)
                        .source("change-service")
                        .data(payload)
                        .build();

        log.info("operation=change-prepared-event.publish stage=start traceId={} correlationId={} eventId={} eventType={} topic={} changeId={}",
                traceId(),
                correlationId,
                envelope.getEventId(),
                envelope.getEventType(),
                TOPIC,
                payload.getChangeId());

        try {
            kafkaTemplate.send(TOPIC, correlationId, envelope);

            log.info("operation=change-prepared-event.publish stage=success traceId={} correlationId={} eventId={} eventType={} topic={} changeId={}",
                    traceId(),
                    correlationId,
                    envelope.getEventId(),
                    envelope.getEventType(),
                    TOPIC,
                    payload.getChangeId());
        } catch (Exception e) {
            log.error("operation=change-prepared-event.publish stage=error traceId={} correlationId={} eventId={} topic={} changeId={} message={}",
                    traceId(),
                    correlationId,
                    envelope.getEventId(),
                    TOPIC,
                    payload.getChangeId(),
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
