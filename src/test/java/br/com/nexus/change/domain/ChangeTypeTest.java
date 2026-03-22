package br.com.nexus.change.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ChangeTypeTest {

    private TransactionType productCategory;
    private TransactionType anotherTransactionType;

    @BeforeEach
    void setUp() {
        productCategory = TransactionType.builder()
                .id(UUID.randomUUID())
                .name("Beverages")
                .build();

        anotherTransactionType = TransactionType.builder()
                .id(UUID.randomUUID())
                .name("Snacks")
                .build();
    }

    @Test
    void testGetters() {
        assertEquals(UUID.randomUUID(), productCategory.getId());
        assertEquals("Beverages", productCategory.getName());
    }

    @Test
    void testSetters() {
        UUID newId = UUID.randomUUID();
        productCategory.setId(newId);
        productCategory.setName("Snacks");

        assertEquals(newId, productCategory.getId());
        assertEquals("Snacks", productCategory.getName());
    }

    @Test
    void testEquals() {
        UUID newId = UUID.randomUUID();
        TransactionType copy = TransactionType.builder()
                .id(newId)
                .name("Beverages")
                .build();

        assertEquals(productCategory, copy);
    }

    @Test
    void testHashCode() {
        UUID newId = UUID.randomUUID();
        TransactionType copy = TransactionType.builder()
                .id(newId)
                .name("Beverages")
                .build();

        assertEquals(productCategory.hashCode(), copy.hashCode());
    }

    @Test
    void testToString() {
        String expected = "TransactionType(id=1, name=Beverages)";
        assertEquals(expected, productCategory.toString());
    }

    @Test
    void testUpdate() {
        UUID newId = UUID.randomUUID();
        productCategory.update(newId, anotherTransactionType);

        assertEquals(newId, productCategory.getId());
        assertEquals("Snacks", productCategory.getName());
    }

    @Test
    void testNoArgsConstructor() {
        TransactionType newTransactionType = new TransactionType();
        assertNotNull(newTransactionType);
    }

    @Test
    void testAllArgsConstructor() {
        UUID newId = UUID.randomUUID();
        TransactionType newTransactionType = new TransactionType(newId, "Frozen Foods");
        assertEquals(newId, newTransactionType.getId());
        assertEquals("Frozen Foods", newTransactionType.getName());
    }
}
