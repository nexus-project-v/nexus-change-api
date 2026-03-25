package br.com.nexus.change.core.domain;

import br.com.nexus.change.core.domain.change.Change;
import br.com.nexus.change.core.domain.change.ChangeLog;
import br.com.nexus.change.core.domain.component.ChangeComponent;
import br.com.nexus.change.core.domain.schedule.ScheduledWindow;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DomainUpdateMethodsTest {

    @Test
    void changeUpdateShouldCopyAllMutableFields() {
        UUID id = UUID.randomUUID();
        UUID componentId = UUID.randomUUID();

        Change source = Change.builder()
                .title("Deploy API v2")
                .description("Upgrade")
                .componentId(componentId)
                .environment("PROD")
                .changeType("NORMAL")
                .requestBy("user@nexus.com")
                .build();

        Change target = new Change();
        target.update(id, source);

        assertEquals(id, target.getId());
        assertEquals("Deploy API v2", target.getTitle());
        assertEquals("Upgrade", target.getDescription());
        assertEquals(componentId, target.getComponentId());
        assertEquals("PROD", target.getEnvironment());
        assertEquals("NORMAL", target.getChangeType());
        assertEquals("user@nexus.com", target.getRequestBy());
    }

    @Test
    void changeLogUpdateShouldCopyAllMutableFields() {
        UUID id = UUID.randomUUID();
        UUID changeId = UUID.randomUUID();

        ChangeLog source = ChangeLog.builder()
                .changeStatus("ROLLBACK")
                .changeId(changeId)
                .build();

        ChangeLog target = new ChangeLog();
        target.update(id, source);

        assertEquals(id, target.getId());
        assertEquals("ROLLBACK", target.getChangeStatus());
        assertEquals(changeId, target.getChangeId());
    }

    @Test
    void changeComponentUpdateShouldCopyAllMutableFields() {
        UUID id = UUID.randomUUID();

        ChangeComponent source = ChangeComponent.builder()
                .name("core-api")
                .version("1.2.3")
                .build();

        ChangeComponent target = new ChangeComponent();
        target.update(id, source);

        assertEquals(id, target.getId());
        assertEquals("core-api", target.getName());
        assertEquals("1.2.3", target.getVersion());
    }

    @Test
    void scheduledWindowUpdateShouldCopyAllMutableFields() {
        UUID id = UUID.randomUUID();
        UUID changeId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.of(2026, 3, 24, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 3, 24, 12, 0);

        ScheduledWindow source = ScheduledWindow.builder()
                .responsible("ops@nexus.com")
                .start(start)
                .end(end)
                .changeId(changeId)
                .build();

        ScheduledWindow target = new ScheduledWindow();
        target.update(id, source);

        assertEquals(id, target.getId());
        assertEquals("ops@nexus.com", target.getResponsible());
        assertEquals(start, target.getStart());
        assertEquals(end, target.getEnd());
        assertEquals(changeId, target.getChangeId());
    }
}

