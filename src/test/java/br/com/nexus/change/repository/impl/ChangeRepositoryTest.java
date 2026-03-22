package br.com.nexus.change.repository.impl;

import br.com.nexus.change.infrastructure.entity.change.TransactionEntity;
import br.com.nexus.change.infrastructure.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class ChangeRepositoryTest {

    @Mock
    private TransactionRepository productRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByCode() {
        // Arrange
        TransactionEntity product = new TransactionEntity();
        product.setId(UUID.randomUUID());
        product.setCode("ABC123");

        when(productRepository.findByCode(anyString())).thenReturn(product);

        // Act
        TransactionEntity foundTransaction = productRepository.findByCode("ABC123");

        // Assert
        assertNotNull(foundTransaction);
        assertEquals("ABC123", foundTransaction.getCode());
    }

    @Test
    void testFindByCode_NotFound() {
        // Arrange
        when(productRepository.findByCode(anyString())).thenReturn(null);

        // Act
        TransactionEntity foundTransaction = productRepository.findByCode("NON_EXISTENT_CODE");

        // Assert
        assertNull(foundTransaction);
    }
}
