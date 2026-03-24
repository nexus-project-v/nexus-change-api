package br.com.nexus.change.repository;

import br.com.nexus.change.infrastructure.entity.change.ChangeEntity;
import br.com.nexus.change.infrastructure.entity.change.ChangeStatus;
import br.com.nexus.change.infrastructure.entity.change.ChangeType;
import br.com.nexus.change.infrastructure.entity.change.Environment;
import br.com.nexus.change.infrastructure.entity.scheduled.ScheduledWindowEntity;
import br.com.nexus.change.infrastructure.repository.ChangeRepository;
import br.com.nexus.change.infrastructure.repository.ScheduledWindowRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@DataJpaTest
@ImportAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("classpath:application-test.properties")
@DisplayName("ScheduledWindowRepository tests")
class ScheduledWindowRepositoryTest {

    @Autowired
    private ScheduledWindowRepository scheduledWindowRepository;

    @Autowired
    private ChangeRepository changeRepository;

    private ChangeEntity changeEntity;

    @BeforeEach
    void setUp() {
        log.info("Cleaning up database...");
        scheduledWindowRepository.deleteAll();
        changeRepository.deleteAll();

        log.info("Setting up base change...");
        this.changeEntity = changeRepository.save(buildChange("Deploy EKS 1.29"));
        log.info("Base ChangeEntity saved: {}", this.changeEntity.getId());
    }

    // -------------------------------------------------------------------------
    // Factory helpers
    // -------------------------------------------------------------------------

    private ChangeEntity buildChange(String title) {
        return ChangeEntity.builder()
                .title(title)
                .description("Descrição de " + title)
                .environment(Environment.PROD)
                .changeType(ChangeType.NORMAL)
                .changeStatus(ChangeStatus.DRAFT)
                .requestBy("user@nexus.com")
                .build();
    }

    private ScheduledWindowEntity buildWindow(ChangeEntity change, LocalDateTime start, LocalDateTime end) {
        return ScheduledWindowEntity.builder()
                .id(UUID.randomUUID())
                .responsible("time-infra@nexus.com")
                .start(start)
                .end(end)
                .changeEntity(change)
                .build();
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should save a scheduled window")
    void should_save_scheduled_window() {
        log.info("Test: should_save_scheduled_window");

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(2);

        ScheduledWindowEntity saved = scheduledWindowRepository.save(
                buildWindow(this.changeEntity, start, end)
        );

        log.info("ScheduledWindowEntity saved: {}", saved.getId());
        assertNotNull(saved.getId());
        assertThat(saved.getResponsible()).isEqualTo("time-infra@nexus.com");
        assertThat(saved.getStart()).isEqualTo(start);
        assertThat(saved.getEnd()).isEqualTo(end);
        assertThat(saved.getChangeEntity().getId()).isEqualTo(this.changeEntity.getId());
    }

    @Test
    @DisplayName("Should find scheduled window by id")
    void should_find_scheduled_window_by_id() {
        log.info("Test: should_find_scheduled_window_by_id");

        ScheduledWindowEntity saved = scheduledWindowRepository.save(
                buildWindow(this.changeEntity, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(4))
        );

        Optional<ScheduledWindowEntity> found = scheduledWindowRepository.findById(saved.getId());

        log.info("ScheduledWindowEntity found: {}", found.isPresent());
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getResponsible()).isEqualTo("time-infra@nexus.com");
    }

    @Test
    @DisplayName("Should return empty when id does not exist")
    void should_return_empty_when_id_not_found() {
        log.info("Test: should_return_empty_when_id_not_found");

        Optional<ScheduledWindowEntity> found = scheduledWindowRepository.findById(UUID.randomUUID());

        log.info("ScheduledWindowEntity found: {}", found.isPresent());
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should list all scheduled windows")
    void should_find_all_scheduled_windows() {
        log.info("Test: should_find_all_scheduled_windows");

        LocalDateTime base = LocalDateTime.now().plusDays(1);

        scheduledWindowRepository.save(buildWindow(this.changeEntity, base, base.plusHours(1)));
        scheduledWindowRepository.save(buildWindow(this.changeEntity, base.plusHours(2), base.plusHours(4)));
        scheduledWindowRepository.save(buildWindow(this.changeEntity, base.plusHours(5), base.plusHours(7)));

        List<ScheduledWindowEntity> all = scheduledWindowRepository.findAll();

        log.info("Total scheduled windows found: {}", all.size());
        assertThat(all).hasSize(3);
    }

    @Test
    @DisplayName("Should update scheduled window using update method")
    void should_update_scheduled_window() {
        log.info("Test: should_update_scheduled_window");

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(2);

        ScheduledWindowEntity saved = scheduledWindowRepository.save(
                buildWindow(this.changeEntity, start, end)
        );

        LocalDateTime newStart = start.plusDays(1);
        LocalDateTime newEnd = end.plusDays(1);

        ScheduledWindowEntity patch = ScheduledWindowEntity.builder()
                .responsible("time-seguranca@nexus.com")
                .start(newStart)
                .end(newEnd)
                .changeEntity(this.changeEntity)
                .build();

        saved.update(saved.getId(), patch);
        ScheduledWindowEntity updated = scheduledWindowRepository.save(saved);

        log.info("ScheduledWindowEntity updated: {}", updated.getId());
        assertThat(updated.getResponsible()).isEqualTo("time-seguranca@nexus.com");
        assertThat(updated.getStart()).isEqualTo(newStart);
        assertThat(updated.getEnd()).isEqualTo(newEnd);
    }

    @Test
    @DisplayName("Should delete scheduled window by id")
    void should_delete_scheduled_window_by_id() {
        log.info("Test: should_delete_scheduled_window_by_id");

        ScheduledWindowEntity saved = scheduledWindowRepository.save(
                buildWindow(this.changeEntity, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2))
        );

        scheduledWindowRepository.deleteById(saved.getId());
        Optional<ScheduledWindowEntity> deleted = scheduledWindowRepository.findById(saved.getId());

        log.info("ScheduledWindowEntity after delete: {}", deleted.isPresent());
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("Should count all scheduled windows")
    void should_count_all_scheduled_windows() {
        log.info("Test: should_count_all_scheduled_windows");

        LocalDateTime base = LocalDateTime.now().plusDays(2);

        scheduledWindowRepository.save(buildWindow(this.changeEntity, base, base.plusHours(1)));
        scheduledWindowRepository.save(buildWindow(this.changeEntity, base.plusHours(2), base.plusHours(4)));

        long count = scheduledWindowRepository.count();

        log.info("Total count: {}", count);
        assertThat(count).isEqualTo(2L);
    }
}

