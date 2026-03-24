package br.com.nexus.change.application.event.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class DeployFinish {
    private UUID changeId;
    private String changeStatus;
}
