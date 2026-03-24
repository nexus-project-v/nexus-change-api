package br.com.nexus.change.repository;

import br.com.nexus.change.infrastructure.entity.change.ChangeEntity;
import br.com.nexus.change.infrastructure.entity.change.ChangeStatus;
import br.com.nexus.change.infrastructure.entity.change.ChangeType;
import br.com.nexus.change.infrastructure.entity.change.Environment;
import br.com.nexus.change.infrastructure.entity.component.ComponentEntity;
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
@DisplayName("ChangeRepository tests")
class ChangeRepositoryTest {

    @Autowired
    private ChangeRepository changeRepository;

    @Autowired
    private ComponentRepository componentRepository;

    @BeforeEach
    void setUp() {
        log.info("Cleaning up database...");
        changeRepository.deleteAll();
        componentRepository.deleteAll();
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

    private ComponentEntity buildComponent() {
        return ComponentEntity.builder()
                .id(UUID.randomUUID())
                .name("infra-cluster-eks")
                .version("1.29")
                .build();
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should save a change without component")
    void should_save_change_without_component() {
        log.info("Test: should_save_change_without_component");

        ChangeEntity saved = changeRepository.save(buildChange("Upgrade EKS"));

        log.info("ChangeEntity saved: {}", saved);
        assertNotNull(saved.getId());
        assertThat(saved.getTitle()).isEqualTo("Upgrade EKS");
        assertThat(saved.getChangeStatus()).isEqualTo(ChangeStatus.DRAFT);
        assertThat(saved.getRequestBy()).isEqualTo("user@nexus.com");
    }

    @Test
    @DisplayName("Should save a change linked to a component")
    void should_save_change_with_component() {
        log.info("Test: should_save_change_with_component");

        ComponentEntity component = componentRepository.save(buildComponent());
        ChangeEntity change = buildChange("Atualização WAF");
        change.setComponentEntity(component);

        ChangeEntity saved = changeRepository.save(change);

        log.info("ChangeEntity saved with component: {}", saved);
        assertNotNull(saved.getId());
        assertThat(saved.getComponentEntity()).isNotNull();
        assertThat(saved.getComponentEntity().getName()).isEqualTo("infra-cluster-eks");
    }

    @Test
    @DisplayName("Should find change by id")
    void should_find_change_by_id() {
        log.info("Test: should_find_change_by_id");

        ChangeEntity saved = changeRepository.save(buildChange("Rollout API v2"));

        Optional<ChangeEntity> found = changeRepository.findById(saved.getId());

        log.info("ChangeEntity found: {}", found);
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Rollout API v2");
    }

    @Test
    @DisplayName("Should return empty optional when id not found")
    void should_return_empty_when_id_not_found() {
        log.info("Test: should_return_empty_when_id_not_found");

        Optional<ChangeEntity> found = changeRepository.findById(UUID.randomUUID());

        log.info("ChangeEntity found: {}", found);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should list all changes")
    void should_find_all_changes() {
        log.info("Test: should_find_all_changes");

        changeRepository.save(buildChange("Deploy Backend"));
        changeRepository.save(buildChange("Deploy Frontend"));
        changeRepository.save(buildChange("Patch Banco"));

        List<ChangeEntity> all = changeRepository.findAll();

        log.info("Total changes found: {}", all.size());
        assertThat(all).hasSize(3);
    }

    @Test
    @DisplayName("Should update a change using applyChanges")
    void should_update_change_with_apply_changes() {
        log.info("Test: should_update_change_with_apply_changes");

        ChangeEntity saved = changeRepository.save(buildChange("Change Original"));

        ChangeEntity patch = buildChange("Change Atualizada");
        patch.setChangeStatus(ChangeStatus.VALIDATED);
        patch.setEnvironment(Environment.HOM);

        saved.applyChanges(saved.getId(), patch);
        ChangeEntity updated = changeRepository.save(saved);

        log.info("ChangeEntity updated: {}", updated);
        assertThat(updated.getTitle()).isEqualTo("Change Atualizada");
        assertThat(updated.getChangeStatus()).isEqualTo(ChangeStatus.VALIDATED);
        assertThat(updated.getEnvironment()).isEqualTo(Environment.HOM);
    }

    @Test
    @DisplayName("Should delete change by id")
    void should_delete_change_by_id() {
        log.info("Test: should_delete_change_by_id");

        ChangeEntity saved = changeRepository.save(buildChange("Change para deletar"));

        changeRepository.deleteById(saved.getId());
        Optional<ChangeEntity> deleted = changeRepository.findById(saved.getId());

        log.info("ChangeEntity after delete: {}", deleted);
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("Should count all changes in repository")
    void should_count_all_changes() {
        log.info("Test: should_count_all_changes");

        changeRepository.save(buildChange("Change A"));
        changeRepository.save(buildChange("Change B"));

        long count = changeRepository.count();

        log.info("Total count: {}", count);
        assertThat(count).isEqualTo(2L);
    }
}

