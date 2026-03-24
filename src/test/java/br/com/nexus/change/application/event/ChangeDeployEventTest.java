package br.com.nexus.change.application.event;

import br.com.nexus.change.application.event.dto.DeployFinish;
import br.com.nexus.change.application.event.dto.EventEnvelope;
import br.com.nexus.change.core.ports.in.change.UpdateChangePort;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChangeDeployEventTest {

    @Mock
    private UpdateChangePort updateChangePort;

    @InjectMocks
    private ChangeDeployEvent changeDeployEvent;

    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(ChangeDeployEvent.class);
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

    private EventEnvelope<DeployFinish> envelope(UUID changeId, String changeStatus) {
        DeployFinish data = new DeployFinish();
        data.setChangeId(changeId);
        data.setChangeStatus(changeStatus);

        return EventEnvelope.<DeployFinish>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("ORDER_READY_FOR_PRODUCTION")
                .version("v1")
                .occurredAt(Instant.now())
                .correlationId(UUID.randomUUID().toString())
                .source("change-service")
                .data(data)
                .build();
    }

    private EventEnvelope<DeployFinish> envelopeWithoutData() {
        return EventEnvelope.<DeployFinish>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("ORDER_READY_FOR_PRODUCTION")
                .version("v1")
                .occurredAt(Instant.now())
                .correlationId(UUID.randomUUID().toString())
                .source("change-service")
                .data(null)
                .build();
    }

    @Test
    @DisplayName("consumer should update change status when payload is valid")
    void consumer_shouldUpdateChangeStatusWhenPayloadIsValid() {
        UUID changeId = UUID.randomUUID();
        String changeStatus = "DEPLOYED";

        changeDeployEvent.consumer(envelope(changeId, changeStatus));

        verify(updateChangePort, times(1)).updateStatus(changeId, changeStatus);
    }

    @Test
    @DisplayName("consumer should not call update port when payload data is null")
    void consumer_shouldNotCallUpdatePortWhenPayloadDataIsNull() {
        changeDeployEvent.consumer(envelopeWithoutData());

        verify(updateChangePort, never()).updateStatus(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString());
        assertThat(logMessages()).anyMatch(message -> message.contains("stage=empty_payload"));
    }

    @Test
    @DisplayName("consumer should rethrow exception when update status fails")
    void consumer_shouldRethrowExceptionWhenUpdateFails() {
        UUID changeId = UUID.randomUUID();
        String changeStatus = "ROLLBACK";
        RuntimeException exception = new RuntimeException("update failed");

        doThrow(exception).when(updateChangePort).updateStatus(changeId, changeStatus);

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> changeDeployEvent.consumer(envelope(changeId, changeStatus)));

        assertThat(thrown).isSameAs(exception);
        verify(updateChangePort, times(1)).updateStatus(changeId, changeStatus);
        assertThat(logMessages()).anyMatch(message -> message.contains("stage=error"));
    }

    @Test
    @DisplayName("consumer should log provided traceId from MDC")
    void consumer_shouldLogProvidedTraceIdFromMdc() {
        UUID changeId = UUID.randomUUID();
        MDC.put("traceId", "trace-123");

        changeDeployEvent.consumer(envelope(changeId, "DEPLOYED"));

        assertThat(logMessages()).anyMatch(message -> message.contains("traceId=trace-123"));
    }

    @Test
    @DisplayName("consumer should log N/A when traceId is absent")
    void consumer_shouldLogNaWhenTraceIdIsAbsent() {
        UUID changeId = UUID.randomUUID();

        changeDeployEvent.consumer(envelope(changeId, "DEPLOYED"));

        assertThat(logMessages()).anyMatch(message -> message.contains("traceId=N/A"));
    }

    private List<String> logMessages() {
        return listAppender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .toList();
    }
}

