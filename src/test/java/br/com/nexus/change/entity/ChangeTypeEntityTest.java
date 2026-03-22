package br.com.nexus.change.entity;

import br.com.nexus.change.infrastructure.entity.component.ComponentEntity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ChangeTypeEntityTest {

    @Disabled
    void testUpdate() {
        // Arrange
        UUID id = UUID.randomUUID();
        TransactionType productCategory = new TransactionType();
        productCategory.setName("Updated Name");

        ComponentEntity productCategoryEntity = new ComponentEntity();
        productCategoryEntity.setId(id);
        productCategoryEntity.setName("Old Name");

        // Act
        productCategoryEntity.update(id, productCategoryEntity);

        // Assert
        assertEquals(id, productCategoryEntity.getId());
        assertEquals("Updated Name", productCategoryEntity.getName());
    }

    @Test
    void testGettersAndSetters() {
        UUID id = UUID.randomUUID();
        TransactionType productCategory = new TransactionType();
        productCategory.setId(id);
        productCategory.setName("Updated Name");

        assertThat(productCategory.getId()).isEqualTo(id);
        assertThat(productCategory.getName()).isEqualTo("Updated Name");
    }

    @Test
    void testToString() {
        UUID id = UUID.randomUUID();
        TransactionType productCategory = TransactionType.builder()
                .id(id)
                .name("Updated Name")
                .build();

        String expected = "TransactionType(id="+id+", name=Updated Name)";
        assertEquals(productCategory.toString(), expected);
    }
}
