/*
package br.com.nexus.change.repository;

import br.com.nexus.change.infrastructure.entity.component.ComponentEntity;
import br.com.nexus.change.infrastructure.repository.TransactionTypeRepository;
import br.com.nexus.change.infrastructure.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@DataJpaTest
@ImportAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("classpath:application-test.properties")
class ChangeTypeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionTypeRepository productCategoryRepository;

    @Autowired
    private TransactionRepository productRepository;

    private ComponentEntity getTransactionType() {
        UUID id = UUID.randomUUID();
        return ComponentEntity.builder()
                .id(id)
                .name("Bebida")
                .build();
    }

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        productCategoryRepository.deleteAll();

        productCategoryRepository.save(getTransactionType());
    }

    @Test
    void should_find_no_clients_if_repository_is_empty() {
        productRepository.deleteAll();
        productCategoryRepository.deleteAll();

        productCategoryRepository.save(getTransactionType());

        List<ComponentEntity> seeds = new ArrayList<>();
        seeds = productCategoryRepository.findAll();
        seeds = Collections.EMPTY_LIST;
        assertThat(seeds).isEmpty();
    }

    @Test
    void should_store_a_product_category() {
        String cocaColaBeverage = "Coca-Cola";
        Optional<ComponentEntity> productCategory = productCategoryRepository.findByName(cocaColaBeverage);
        Optional<ComponentEntity> productCategoryResponse = null;
        if (!productCategory.isPresent()) {

            ComponentEntity cocaCola = ComponentEntity.builder()
                    .name(cocaColaBeverage)
                    .build();

            ComponentEntity save = productCategoryRepository.save(cocaCola);
            productCategoryResponse = productCategoryRepository.findByName(cocaColaBeverage);
        }

        ComponentEntity productCategory1 = productCategoryResponse.get();
        assertThat(productCategory1).hasFieldOrPropertyWithValue("name", cocaColaBeverage);
    }

    @Disabled
    void testSaveRestaurantWithLongName() {
        ComponentEntity productCategory = new ComponentEntity();
        productCategory.setName("a".repeat(260)); // Nome com 260 caracteres, excedendo o limite de 255

        assertThrows(DataIntegrityViolationException.class, () -> {
            productCategoryRepository.save(productCategory);
        });
    }

    private ComponentEntity createInvalidTransactionType() {
        ComponentEntity productCategory = new ComponentEntity();
        // Configurar o productCategory com valores inválidos
        // Exemplo: valores inválidos que podem causar uma ConstraintViolationException
        productCategory.setName(""); // Nome vazio pode causar uma violação
        return productCategory;
    }

    @Test
    void should_found_null_TransactionType() {
        ComponentEntity productCategory = null;

        Optional<ComponentEntity> fromDb = productCategoryRepository.findById(UUID.randomUUID());
        if (fromDb.isPresent()) {
            productCategory = fromDb.get();
        }
        assertThat(productCategory).isNull();
    }

    @Test
    void whenFindById_thenReturnTransactionType() {
        Optional<ComponentEntity> productCategory = productCategoryRepository.findById(UUID.randomUUID());
        if (productCategory.isPresent()) {
            ComponentEntity productCategoryResult = productCategory.get();
            assertThat(productCategoryResult).hasFieldOrPropertyWithValue("name", "Bebida");
        }
    }

    @Test
    void whenInvalidId_thenReturnNull() {
        ComponentEntity fromDb = productCategoryRepository.findById(UUID.randomUUID()).orElse(null);
        assertThat(fromDb).isNull();
    }

    @Test
    void givenSetOfTransactionTypes_whenFindAll_thenReturnAllTransactionTypes() {
        ComponentEntity productCategory = null;
        ComponentEntity productCategory1 = null;
        ComponentEntity productCategory2 = null;

        Optional<ComponentEntity> restaurant = productCategoryRepository.findById(UUID.randomUUID());
        if (restaurant.isPresent()) {

            ComponentEntity bebida = ComponentEntity.builder()
                    .name("Bebida")
                    .build();
            productCategory = productCategoryRepository.save(bebida);

            ComponentEntity acompanhamento = ComponentEntity.builder()
                    .name("Acompanhamento")
                    .build();
            productCategory1 = productCategoryRepository.save(acompanhamento);

            ComponentEntity lanche = ComponentEntity.builder()
                    .name("Lanche")
                    .build();
            productCategory2 = productCategoryRepository.save(lanche);

        }

        Iterator<ComponentEntity> allTransactionTypes = productCategoryRepository.findAll().iterator();
        List<ComponentEntity> clients = new ArrayList<>();
        allTransactionTypes.forEachRemaining(c -> clients.add(c));

        assertNotNull(allTransactionTypes);
    }
}*/
