package br.com.nexus.change.repository;

import br.com.nexus.change.infrastructure.entity.change.ChangeEntity;
import br.com.nexus.change.infrastructure.entity.change.ChangeLogEntity;
import br.com.nexus.change.infrastructure.entity.change.ChangeStatus;
import br.com.nexus.change.infrastructure.entity.change.ChangeType;
import br.com.nexus.change.infrastructure.entity.change.Environment;
import br.com.nexus.change.infrastructure.entity.component.ComponentEntity;
import br.com.nexus.change.infrastructure.repository.ChangeLogRepository;
import br.com.nexus.change.infrastructure.repository.ChangeRepository;
import br.com.nexus.change.infrastructure.repository.ComponentRepository;
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
@DisplayName("ChangeLogRepository tests")
class ChangeLogRepositoryTest {

    @Autowired
    private ChangeLogRepository changeLogRepository;

    @Autowired
    private ChangeRepository changeRepository;

    @Autowired
    private ComponentRepository componentRepository;

    private ChangeEntity baseChange;

    @BeforeEach
    void setUp() {
        log.info("Cleaning up database...");
        changeLogRepository.deleteAll();
        changeRepository.deleteAll();
        componentRepository.deleteAll();

        ComponentEntity component = componentRepository.save(buildComponent("core-api", "1.0.0"));
        baseChange = changeRepository.save(buildChange("Deploy core-api", component));
    }

    private ComponentEntity buildComponent(String name, String version) {
        return ComponentEntity.builder()
                .id(UUID.randomUUID())
                .name(name)
                .version(version)
                .build();
    }

    private ChangeEntity buildChange(String title, ComponentEntity componentEntity) {
        return ChangeEntity.builder()
                .title(title)
                .description("Descricao de " + title)
                .componentEntity(componentEntity)
                .environment(Environment.PROD)
                .changeType(ChangeType.NORMAL)
                .changeStatus(ChangeStatus.DRAFT)
                .requestBy("user@nexus.com")
                .build();
    }

    private ChangeLogEntity buildChangeLog(ChangeEntity changeEntity, ChangeStatus changeStatus) {
        return ChangeLogEntity.builder()
                .changeEntity(changeEntity)
                .changeStatus(changeStatus)
                .build();
    }

    @Test
    @DisplayName("Should save change log")
    void should_save_change_log() {
        ChangeLogEntity saved = changeLogRepository.save(buildChangeLog(baseChange, ChangeStatus.PREPARED_FOR_DEPLOY));

        assertNotNull(saved.getId());
        assertThat(saved.getChangeStatus()).isEqualTo(ChangeStatus.PREPARED_FOR_DEPLOY);
        assertThat(saved.getChangeEntity().getId()).isEqualTo(baseChange.getId());
    }

    @Test
    @DisplayName("Should find change log by id")
    void should_find_change_log_by_id() {
        ChangeLogEntity saved = changeLogRepository.save(buildChangeLog(baseChange, ChangeStatus.DEPLOYED));

        Optional<ChangeLogEntity> found = changeLogRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getChangeStatus()).isEqualTo(ChangeStatus.DEPLOYED);
    }

    @Test
    @DisplayName("Should return empty optional when id not found")
    void should_return_empty_when_id_not_found() {
        Optional<ChangeLogEntity> found = changeLogRepository.findById(UUID.randomUUID());

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find all change logs by change id")
    void should_find_by_change_entity_id() {
        changeLogRepository.save(buildChangeLog(baseChange, ChangeStatus.CREATED));
        changeLogRepository.save(buildChangeLog(baseChange, ChangeStatus.VALIDATED));

        ComponentEntity secondComponent = componentRepository.save(buildComponent("payments", "2.0.0"));
        ChangeEntity secondChange = changeRepository.save(buildChange("Deploy payments", secondComponent));
        changeLogRepository.save(buildChangeLog(secondChange, ChangeStatus.DEPLOYED));

        List<ChangeLogEntity> logs = changeLogRepository.findByChangeEntityId(baseChange.getId());

        assertThat(logs).hasSize(2);
        assertThat(logs).extracting(ChangeLogEntity::getChangeStatus)
                .containsExactlyInAnyOrder(ChangeStatus.CREATED, ChangeStatus.VALIDATED);
    }

    @Test
    @DisplayName("Should return empty list when change id not found")
    void should_return_empty_list_when_change_id_not_found() {
        List<ChangeLogEntity> logs = changeLogRepository.findByChangeEntityId(UUID.randomUUID());

        assertThat(logs).isEmpty();
    }

    @Test
    @DisplayName("Should update change log using applyChanges")
    void should_update_change_log_with_apply_changes() {
        ChangeLogEntity saved = changeLogRepository.save(buildChangeLog(baseChange, ChangeStatus.CREATED));

        ChangeLogEntity patch = buildChangeLog(baseChange, ChangeStatus.ROLLBACK);
        saved.applyChanges(saved.getId(), patch);

        ChangeLogEntity updated = changeLogRepository.save(saved);

        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getChangeStatus()).isEqualTo(ChangeStatus.ROLLBACK);
    }

    @Test
    @DisplayName("Should delete change log by id")
    void should_delete_change_log_by_id() {
        ChangeLogEntity saved = changeLogRepository.save(buildChangeLog(baseChange, ChangeStatus.CREATED));

        changeLogRepository.deleteById(saved.getId());
        Optional<ChangeLogEntity> found = changeLogRepository.findById(saved.getId());

        assertThat(found).isEmpty();
    }
}

