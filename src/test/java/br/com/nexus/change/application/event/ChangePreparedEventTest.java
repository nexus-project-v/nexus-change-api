package br.com.nexus.change.application.event;

import br.com.nexus.change.application.event.dto.ChangePreparedPayload;
import br.com.nexus.change.application.event.dto.EventEnvelope;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangePreparedEventTest {

    private static final String TOPIC = "orders.ready-for-production.v1";
    private static final String EVENT_TYPE = "ORDER_READY_FOR_PRODUCTION";

    @Mock
    private KafkaTemplate<String, EventEnvelope<ChangePreparedPayload>> kafkaTemplate;

    @InjectMocks
    private ChangePreparedEvent changePreparedEvent;

    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(ChangePreparedEvent.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
        if (logger != null && listAppender != null) {
            logger.detachAppender(listAppender);
            listAppender.stop();
        }
    }

    private ChangePreparedPayload payload(UUID changeId) {
        return ChangePreparedPayload.builder()
                .changeId(changeId)
                .component("core-api")
                .componentVersion("1.2.3")
                .environment("PROD")
                .changeType("NORMAL")
                .changeStatus("CREATED")
                .build();
    }

    @Test
    @DisplayName("publish should send envelope to Kafka with expected fields")
    void publish_shouldSendEnvelopeToKafkaWithExpectedFields() {
        UUID changeId = UUID.randomUUID();
        ChangePreparedPayload payload = payload(changeId);

        when(kafkaTemplate.send(eq(TOPIC), eq(changeId.toString()), org.mockito.ArgumentMatchers.any()))
                .thenReturn(CompletableFuture.completedFuture(null));

        changePreparedEvent.publish(payload);

        ArgumentCaptor<EventEnvelope<ChangePreparedPayload>> envelopeCaptor = ArgumentCaptor.forClass(EventEnvelope.class);
        verify(kafkaTemplate, times(1)).send(eq(TOPIC), eq(changeId.toString()), envelopeCaptor.capture());

        EventEnvelope<ChangePreparedPayload> captured = envelopeCaptor.getValue();
        assertThat(captured).isNotNull();
        assertThat(captured.getEventId()).isNotBlank();
        assertThat(captured.getEventType()).isEqualTo(EVENT_TYPE);
        assertThat(captured.getVersion()).isEqualTo("v1");
        assertThat(captured.getOccurredAt()).isNotNull();
        assertThat(captured.getCorrelationId()).isEqualTo(changeId.toString());
        assertThat(captured.getSource()).isEqualTo("change-service");
        assertThat(captured.getData()).isEqualTo(payload);

        assertThat(logMessages()).anyMatch(message -> message.contains("stage=build_envelope"));
        assertThat(logMessages()).anyMatch(message -> message.contains("stage=start"));
        assertThat(logMessages()).anyMatch(message -> message.contains("stage=success"));
    }

    @Test
    @DisplayName("publish should rethrow exception when Kafka send fails")
    void publish_shouldRethrowExceptionWhenKafkaSendFails() {
        UUID changeId = UUID.randomUUID();
        ChangePreparedPayload payload = payload(changeId);
        RuntimeException exception = new RuntimeException("kafka unavailable");

        when(kafkaTemplate.send(eq(TOPIC), eq(changeId.toString()), org.mockito.ArgumentMatchers.any()))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> changePreparedEvent.publish(payload));

        assertThat(thrown).isSameAs(exception);
        verify(kafkaTemplate, times(1)).send(eq(TOPIC), eq(changeId.toString()), org.mockito.ArgumentMatchers.any());
        assertThat(logMessages()).anyMatch(message -> message.contains("stage=error"));
        assertThat(logMessages()).anyMatch(message -> message.contains("message=kafka unavailable"));
    }

    @Test
    @DisplayName("publish should log provided traceId from MDC")
    void publish_shouldLogProvidedTraceIdFromMdc() {
        UUID changeId = UUID.randomUUID();
        MDC.put("traceId", "trace-123");

        when(kafkaTemplate.send(eq(TOPIC), eq(changeId.toString()), org.mockito.ArgumentMatchers.any()))
                .thenReturn(CompletableFuture.completedFuture(null));

        changePreparedEvent.publish(payload(changeId));

        assertThat(logMessages()).anyMatch(message -> message.contains("traceId=trace-123"));
    }

    @Test
    @DisplayName("publish should log N/A when traceId is absent")
    void publish_shouldLogNaWhenTraceIdIsAbsent() {
        UUID changeId = UUID.randomUUID();

        when(kafkaTemplate.send(eq(TOPIC), eq(changeId.toString()), org.mockito.ArgumentMatchers.any()))
                .thenReturn(CompletableFuture.completedFuture(null));

        changePreparedEvent.publish(payload(changeId));

        assertThat(logMessages()).anyMatch(message -> message.contains("traceId=N/A"));
    }

    private List<String> logMessages() {
        return listAppender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .toList();
    }
}

