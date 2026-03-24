package br.com.nexus.change.application.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeStatusResponse {
    private UUID changeId;
    private String changeStatus;
    private Instant updatedAt;

    private List<TimelineEvent> timeline;
    private List<LogEntry> log_change;
}
