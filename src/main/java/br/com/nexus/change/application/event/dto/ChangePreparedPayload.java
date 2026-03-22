package br.com.nexus.change.application.event.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ChangePreparedPayload {
    private UUID changeId;
    private String component;
    private String componentVersion;
    private String environment;
    private String changeType;
    private String changeStatus;
}
