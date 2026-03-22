package br.com.nexus.change.application.event;

import br.com.nexus.change.application.event.dto.ChangePreparedPayload;
import br.com.nexus.change.application.event.dto.EventEnvelope;
import br.com.nexus.change.core.ports.out.event.ChangeEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class ChangePreparedEventAdapter implements ChangeEventPublisher {

    private final KafkaTemplate<String, EventEnvelope<ChangePreparedPayload>> kafkaTemplate;

    public ChangePreparedEventAdapter(KafkaTemplate<String, EventEnvelope<ChangePreparedPayload>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void publish(ChangePreparedPayload payload) {
        EventEnvelope<ChangePreparedPayload> envelope =
                EventEnvelope.<ChangePreparedPayload>builder()
                        .eventId(UUID.randomUUID().toString())
                        .eventType("ORDER_READY_FOR_PRODUCTION")
                        .version("v1")
                        .occurredAt(Instant.now())
                        .correlationId(payload.getChangeId().toString())
                        .source("order-service")
                        .data(payload)
                        .build();

        kafkaTemplate.send(
                "orders.ready-for-production.v1",
                payload.getChangeId().toString(),
                envelope
        );
    }
}
