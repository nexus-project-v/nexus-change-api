/*
package br.com.nexus.change.repository.impl;

import br.com.nexus.change.infrastructure.entity.component.ComponentEntity;
import br.com.nexus.change.infrastructure.repository.TransactionTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ChangeTypeRepositoryTest {

    @Autowired
    private TransactionTypeRepository productCategoryRepository;

    private ComponentEntity electronics;
    private ComponentEntity books;

    @BeforeEach
    void setUp() {
        electronics = new ComponentEntity();
        electronics.setName("Electronics");

        books = new ComponentEntity();
        books.setName("Books");

        productCategoryRepository.save(electronics);
        productCategoryRepository.save(books);
    }

    @Test
    void testFindByName_found() {
        Optional<ComponentEntity> found = productCategoryRepository.findByName("Electronics");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Electronics");
    }

    @Test
    void testFindByName_notFound() {
        Optional<ComponentEntity> found = productCategoryRepository.findByName("NonExistingCategory");
        assertThat(found).isNotPresent();
    }

    @Test
    void testFindByName_caseInsensitive() {
        Optional<ComponentEntity> found = productCategoryRepository.findByName("electronics");
        assertThat(found).isNotPresent();
    }
}
*/
