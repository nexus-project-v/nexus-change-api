package br.com.nexus.change.core.ports.out.event;

import br.com.nexus.change.application.event.dto.ChangePreparedPayload;

public interface ChangeEventPublisher {
    void publish(ChangePreparedPayload event);
}
