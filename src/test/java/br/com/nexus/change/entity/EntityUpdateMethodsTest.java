package br.com.nexus.change.entity;

import br.com.nexus.change.infrastructure.entity.change.ChangeEntity;
import br.com.nexus.change.infrastructure.entity.change.ChangeLogEntity;
import br.com.nexus.change.infrastructure.entity.change.ChangeStatus;
import br.com.nexus.change.infrastructure.entity.change.ChangeType;
import br.com.nexus.change.infrastructure.entity.change.Environment;
import br.com.nexus.change.infrastructure.entity.component.ComponentEntity;
import br.com.nexus.change.infrastructure.entity.scheduled.ScheduledWindowEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityUpdateMethodsTest {

    @Test
    void changeEntityApplyChangesShouldCopyAllMutableFields() {
        UUID id = UUID.randomUUID();

        ComponentEntity component = ComponentEntity.builder()
                .id(UUID.randomUUID())
                .name("core-api")
                .version("1.0.0")
                .build();

        ChangeEntity source = ChangeEntity.builder()
                .title("Deploy core-api")
                .description("Routine deploy")
                .componentEntity(component)
                .environment(Environment.PROD)
                .changeType(ChangeType.NORMAL)
                .changeStatus(ChangeStatus.CREATED)
                .requestBy("ops@nexus.com")
                .build();

        ChangeEntity target = new ChangeEntity();
        target.applyChanges(id, source);

        assertEquals(id, target.getId());
        assertEquals("Deploy core-api", target.getTitle());
        assertEquals("Routine deploy", target.getDescription());
        assertEquals(component, target.getComponentEntity());
        assertEquals(Environment.PROD, target.getEnvironment());
        assertEquals(ChangeType.NORMAL, target.getChangeType());
        assertEquals(ChangeStatus.CREATED, target.getChangeStatus());
        assertEquals("ops@nexus.com", target.getRequestBy());
    }

    @Test
    void changeLogEntityApplyChangesShouldCopyChangeAndStatus() {
        UUID id = UUID.randomUUID();

        ChangeEntity change = ChangeEntity.builder()
                .id(UUID.randomUUID())
                .title("Deploy")
                .build();

        ChangeLogEntity source = ChangeLogEntity.builder()
                .changeEntity(change)
                .changeStatus(ChangeStatus.ROLLBACK)
                .build();

        ChangeLogEntity target = new ChangeLogEntity();
        target.applyChanges(id, source);

        assertEquals(id, target.getId());
        assertEquals(change, target.getChangeEntity());
        assertEquals(ChangeStatus.ROLLBACK, target.getChangeStatus());
    }

    @Test
    void componentEntityUpdateShouldUpdateIdAndNameOnly() {
        UUID id = UUID.randomUUID();

        ComponentEntity source = ComponentEntity.builder()
                .name("billing-api")
                .version("2.0.0")
                .build();

        ComponentEntity target = ComponentEntity.builder()
                .version("1.0.0")
                .build();

        target.update(id, source);

        assertEquals(id, target.getId());
        assertEquals("billing-api", target.getName());
        // By current implementation, version is intentionally not copied.
        assertEquals("1.0.0", target.getVersion());
    }

    @Test
    void scheduledWindowEntityUpdateShouldCopyAllMutableFields() {
        UUID id = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.of(2026, 3, 24, 22, 0);
        LocalDateTime end = LocalDateTime.of(2026, 3, 24, 23, 0);

        ChangeEntity change = ChangeEntity.builder()
                .id(UUID.randomUUID())
                .title("Deploy")
                .build();

        ScheduledWindowEntity source = ScheduledWindowEntity.builder()
                .responsible("time-sec@nexus.com")
                .start(start)
                .end(end)
                .changeEntity(change)
                .build();

        ScheduledWindowEntity target = new ScheduledWindowEntity();
        target.update(id, source);

        assertEquals(id, target.getId());
        assertEquals("time-sec@nexus.com", target.getResponsible());
        assertEquals(start, target.getStart());
        assertEquals(end, target.getEnd());
        assertEquals(change, target.getChangeEntity());
    }
}

