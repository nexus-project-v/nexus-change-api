package br.com.nexus.change.application.event.dto;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEnvelope<T> {

    private String eventId;
    private String eventType;
    private String version;
    private Instant occurredAt;

    private String correlationId; // 🔥 chave de rastreio
    private String source;        // quem gerou

    private T data;
}