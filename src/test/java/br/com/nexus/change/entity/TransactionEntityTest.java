/*package br.com.nexus.transaction.entity;

import br.com.nexus.transaction.core.domain.Transaction;
import br.com.nexus.transaction.infrastructure.entity.transaction.TransactionEntity;
import br.com.nexus.transaction.infrastructure.entity.transactiontype.TransactionTypeEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionEntityTest {

    @Test
    void testUpdate() {
        // Arrange
        Long id = 1L;
        Transaction transaction = new Transaction();


        TransactionEntity entity = new TransactionEntity();
        entity.setId(2L);
        entity.setName("Old Name");
        entity.setPic("Old Pic");
        entity.setDescription("Old Description");
        entity.setPrice(new BigDecimal("49.99"));

        // Act
        entity.update(id, entity);

        // Assert
        assertEquals(id, entity.getId());
        assertEquals("Old Name", entity.getName());
        assertEquals("Old Pic", entity.getPic());
        assertEquals("Old Description", entity.getDescription());
        assertEquals(new BigDecimal("49.99"), entity.getPrice());
    }

    @Test
    void testGettersAndSetters() {
        TransactionEntity entity = new TransactionEntity();
        entity.setId(1L);
        entity.setName("Old Name");
        entity.setPic("Old Pic");
        entity.setDescription("Old Description");
        entity.setPrice(new BigDecimal("49.99"));

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getName()).isEqualTo("Old Name");
        assertThat(entity.getPic()).isEqualTo("Old Pic");
    }

    @Test
    void testToString() {
        TransactionTypeEntity productCategory = new TransactionTypeEntity();
        productCategory.setId(1L);
        productCategory.setName("Bebida");

        TransactionEntity product = new TransactionEntity();
        product.setId(1L);
        product.setCode("d7d19a26-846f-4808-818b-ffc3495be7bb");
        product.setName("Old Name");
        product.setPic("Old Pic");
        product.setDescription("Old Description");
        product.setPrice(new BigDecimal("49.99"));
        product.setTransactionType(productCategory);

        String expected = "TransactionEntity(id=1, code=d7d19a26-846f-4808-818b-ffc3495be7bb, name=Old Name, pic=Old Pic, description=Old Description, price=49.99, productCategory=TransactionTypeEntity(id=1, name=Bebida), restaurant=RestaurantEntity(id=1, name=Test Restaurant, cnpj=12.345.678/0001-99))";
        assertThat(product).hasToString(expected);
    }

    @Test
    void testEqualsAndHashCode() {
        TransactionEntity product1 = TransactionEntity.builder()
                .id(1L)
                .name("Old Name")
                .pic("Old Pic")
                .description("Old Description")
                .build();

        TransactionEntity product2 = TransactionEntity.builder()
                .id(1L)
                .name("Old Name")
                .pic("Old Pic")
                .description("Old Description")
                .build();

        assertThat(product2).isEqualTo(product2);
        assertThat(product1).hasSameHashCodeAs(product2);
    }
}*/
