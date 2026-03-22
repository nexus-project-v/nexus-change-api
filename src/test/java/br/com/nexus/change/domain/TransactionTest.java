/*package br.com.nexus.transaction.domain;

import br.com.nexus.transaction.core.domain.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionTest {

    @Test
    void testGettersAndSetters() {
        Transaction product = new Transaction();
        product.setId(1L);
        product.setCode("CODE123");
        product.setName("Transaction Name");
        product.setDescription("Transaction Description");
        product.setPrice(new BigDecimal("9.99"));
        product.setPic("pic.jpg");
        product.setTransactionTypeId(2L);

        assertEquals(1L, product.getId());
        assertEquals("CODE123", product.getCode());
        assertEquals("Transaction Name", product.getName());
        assertEquals("Transaction Description", product.getDescription());
        assertEquals(new BigDecimal("9.99"), product.getPrice());
        assertEquals("pic.jpg", product.getPic());
        assertEquals(2L, product.getTransactionTypeId());
    }

    @Test
    void testBuilder() {
        Transaction product = Transaction.builder()
                .id(1L)
                .code("CODE123")
                .name("Transaction Name")
                .description("Transaction Description")
                .price(new BigDecimal("9.99"))
                .pic("pic.jpg")
                .transactionTypeId(2L)
               .build();

        assertEquals(1L, product.getId());
        assertEquals("CODE123", product.getCode());
        assertEquals("Transaction Name", product.getName());
        assertEquals("Transaction Description", product.getDescription());
        assertEquals(new BigDecimal("9.99"), product.getPrice());
        assertEquals("pic.jpg", product.getPic());
        assertEquals(2L, product.getTransactionTypeId());
    }

    @Test
    void testUpdate() {
        Transaction product = new Transaction();
        product.setId(1L);
        product.setCode("CODE123");
        product.setName("Transaction Name");
        product.setDescription("Transaction Description");
        product.setPrice(new BigDecimal("9.99"));
        product.setPic("pic.jpg");
        product.setTransactionTypeId(2L);

        Transaction newTransaction = new Transaction();
        newTransaction.setCode("NEWCODE");
        newTransaction.setName("New Transaction Name");
        newTransaction.setDescription("New Transaction Description");
        newTransaction.setPrice(new BigDecimal("19.99"));
        newTransaction.setPic("newpic.jpg");
        newTransaction.setTransactionTypeId(4L);

        product.update(2L, newTransaction);

        assertEquals(2L, product.getId());
        assertEquals("NEWCODE", product.getCode());
        assertEquals("New Transaction Name", product.getName());
        assertEquals("New Transaction Description", product.getDescription());
        assertEquals(new BigDecimal("19.99"), product.getPrice());
        assertEquals("newpic.jpg", product.getPic());
        assertEquals(4L, product.getTransactionTypeId());
    }

    @Test
    void testNoArgsConstructor() {
        Transaction product = new Transaction();
        assertNotNull(product);
    }

    @Test
    void testAllArgsConstructor() {
        Transaction product = new Transaction(1L, "CODE123", "Transaction Name", "Transaction Description", new BigDecimal("9.99"), "pic.jpg", 2L);
        assertEquals(1L, product.getId());
        assertEquals("CODE123", product.getCode());
        assertEquals("Transaction Name", product.getName());
        assertEquals("Transaction Description", product.getDescription());
        assertEquals(new BigDecimal("9.99"), product.getPrice());
        assertEquals("pic.jpg", product.getPic());
        assertEquals(2L, product.getTransactionTypeId());
    }
}*/