package br.com.nexus.change.repository;

import br.com.nexus.change.infrastructure.entity.component.ComponentEntity;
import br.com.nexus.change.infrastructure.repository.ComponentRepository;
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

@DataJpaTest
@ImportAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("classpath:application-test.properties")
@DisplayName("ComponentRepository tests")
class ComponentRepositoryTest {

    @Autowired
    private ComponentRepository componentRepository;

    @BeforeEach
    void setUp() {
        componentRepository.deleteAll();
    }

    private ComponentEntity createComponent(String name, String version) {
        return ComponentEntity.builder()
                .id(UUID.randomUUID())
                .name(name)
                .version(version)
                .build();
    }

    @Test
    @DisplayName("Should save component")
    void should_save_component() {
        ComponentEntity component = createComponent("infra-cluster-eks", "1.29");

        ComponentEntity saved = componentRepository.save(component);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("infra-cluster-eks");
        assertThat(saved.getVersion()).isEqualTo("1.29");
    }

    @Test
    @DisplayName("Should find component by id")
    void should_find_component_by_id() {
        ComponentEntity saved = componentRepository.save(createComponent("api-gateway", "2.0.1"));

        Optional<ComponentEntity> found = componentRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("api-gateway");
    }

    @Test
    @DisplayName("Should list all components")
    void should_list_all_components() {
        componentRepository.save(createComponent("payments", "1.0.0"));
        componentRepository.save(createComponent("orders", "1.1.0"));

        List<ComponentEntity> all = componentRepository.findAll();

        assertThat(all).hasSize(2);
    }

    @Test
    @DisplayName("Should update component")
    void should_update_component() {
        ComponentEntity saved = componentRepository.save(createComponent("legacy-api", "1.0.0"));

        saved.setName("legacy-api-v2");
        saved.setVersion("2.0.0");
        ComponentEntity updated = componentRepository.save(saved);

        assertThat(updated.getName()).isEqualTo("legacy-api-v2");
        assertThat(updated.getVersion()).isEqualTo("2.0.0");
    }

    @Test
    @DisplayName("Should delete component")
    void should_delete_component() {
        ComponentEntity saved = componentRepository.save(createComponent("to-delete", "0.0.1"));

        componentRepository.deleteById(saved.getId());

        Optional<ComponentEntity> found = componentRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when id does not exist")
    void should_return_empty_when_id_does_not_exist() {
        Optional<ComponentEntity> found = componentRepository.findById(UUID.randomUUID());

        assertThat(found).isEmpty();
    }
}

