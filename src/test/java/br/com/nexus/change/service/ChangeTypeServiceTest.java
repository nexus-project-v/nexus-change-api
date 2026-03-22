/*
package br.com.nexus.change.service;

import br.com.nexus.change.application.database.mapper.TransactionTypeMapper;
import br.com.nexus.change.core.ports.out.TransactionTypeRepositoryPort;
import br.com.nexus.change.core.service.TransactionTypeService;
import br.com.nexus.change.infrastructure.entity.component.ComponentEntity;
import br.com.nexus.change.infrastructure.repository.TransactionTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.DataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ChangeTypeServiceTest {

    @InjectMocks
    TransactionTypeService productCategoryService;

    @Mock
    TransactionTypeRepositoryPort productCategoryRepository;

    @Mock
    TransactionTypeRepository repository;

    @Mock
    TransactionTypeMapper mapper;

    @Mock
    CreateTransactionTypePort createTransactionTypePort;

    @Mock
    DeleteTransactionTypePort deleteTransactionTypePort;

    @Mock
    FindByIdTransactionTypePort findByIdTransactionTypePort;

    @Mock
    FindTransactionTypesPort findTransactionCategoriesPort;

    @Mock
    UpdateTransactionTypePort updateTransactionTypePort;

    private ComponentEntity getTransactionTypeEntity() {
        return ComponentEntity.builder()
                .name("Bebida")
                .build();
    }

    private ComponentEntity getTransactionTypeEntity1() {
        return ComponentEntity.builder()
                .name("Bebida 1")
                .build();
    }

    private ComponentEntity getTransactionTypeEntity2() {
        return ComponentEntity.builder()
                .name("Bebida 2")
                .build();
    }

    private TransactionType getTransactionType() {
        return TransactionType.builder()
                .name("Bebida")
                .build();
    }

    private TransactionType getTransactionType1() {
        return TransactionType.builder()
                .name("Bebida 1")
                .build();
    }

    private TransactionType getTransactionType2() {
        return TransactionType.builder()
                .name("Bebida 2")
                .build();
    }

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getAllTransactionTypesTest() {
        List<TransactionType> productCategorys = new ArrayList<>();
        List<ComponentEntity> listEntity = new ArrayList<>();

        TransactionType client = getTransactionType();
        TransactionType client1 = getTransactionType1();
        TransactionType client2 = getTransactionType2();

        ComponentEntity clientEntity = getTransactionTypeEntity();
        ComponentEntity clientEntity1 = getTransactionTypeEntity1();
        ComponentEntity clientEntity2 = getTransactionTypeEntity2();

        productCategorys.add(client);
        productCategorys.add(client1);
        productCategorys.add(client2);

        listEntity.add(clientEntity);
        listEntity.add(clientEntity1);
        listEntity.add(clientEntity2);

        when(productCategoryService.findAll()).thenReturn(productCategorys);

        List<TransactionType> productCategoryList = productCategoryService.findAll();

        assertNotNull(productCategoryList);
    }

    @Test
    void getTransactionTypeByIdTest() {
        UUID id = UUID.randomUUID();
        TransactionType productCategory1 = getTransactionType();
        when(productCategoryService.findById(id)).thenReturn(productCategory1);

        TransactionType productCategory = productCategoryService.findById(id);

        assertEquals("Bebida", productCategory.getName());
    }

    @Test
    void getFindTransactionTypeByShortIdTest() {
        UUID id = UUID.randomUUID();
        TransactionType productCategory = getTransactionType();
        when(productCategoryService.findById(id)).thenReturn(productCategory);

        TransactionType result = productCategoryService.findById(id);

        assertEquals("Bebida", result.getName());
    }

    @Test
    void createTransactionTypeTest() {
        UUID id = UUID.randomUUID();
        TransactionType productCategory = getTransactionType();
        TransactionType productCategoryResult = getTransactionType();
        productCategoryResult.setId(id);

        when(productCategoryService.save(productCategory)).thenReturn(productCategoryResult);
        TransactionType save = productCategoryService.save(productCategory);

        assertNotNull(save);
        //verify(productCategoryRepository, times(1)).save(productCategory);
    }

    @Test
    void testSaveRestaurantWithLongName() {
        TransactionType productCategory = new TransactionType();
        productCategory.setName("a".repeat(260)); // Nome com 260 caracteres, excedendo o limite de 255

        // Simulando o lançamento de uma exceção
        doThrow(new DataException("Value too long for column 'name'", null)).when(productCategoryRepository).save(productCategory);

        assertThrows(DataException.class, () -> {
            productCategoryRepository.save(productCategory);
        });
    }

    @Test
    void testRemoveRestaurant_Success() {
        UUID id = UUID.randomUUID();
        TransactionType productCategory = getTransactionType();
        productCategory.setId(id);

        when(productCategoryService.findById(id)).thenReturn(productCategory);
        boolean result = productCategoryService.remove(id);
        assertTrue(result);
    }

    @Test
    void testRemove_Exception() {
        UUID id = UUID.randomUUID();

        boolean result = productCategoryService.remove(id);
        assertFalse(result);
        verify(productCategoryRepository, never()).remove(id);
    }

    @Test
    void testCreateTransactionType() {
        TransactionType productCategory = getTransactionType();
        when(createTransactionTypePort.save(productCategory)).thenReturn(productCategory);

        TransactionType result = createTransactionTypePort.save(productCategory);

        assertNotNull(result);
        assertEquals("Bebida", result.getName());
    }

    @Test
    void testDeleteTransactionType() {
        UUID id = UUID.randomUUID();
        when(deleteTransactionTypePort.remove(id)).thenReturn(true);

        boolean result = deleteTransactionTypePort.remove(id);

        assertTrue(result);
    }

    @Test
    void testFindByIdTransactionType() {
        UUID id = UUID.randomUUID();
        TransactionType productCategory = getTransactionType();
        when(findByIdTransactionTypePort.findById(id)).thenReturn(productCategory);

        TransactionType result = findByIdTransactionTypePort.findById(id);

        assertNotNull(result);
        assertEquals("Bebida", result.getName());
    }

    @Test
    void testFindTransactionCategories() {
        List<TransactionType> productCategories = new ArrayList<>();
        productCategories.add(getTransactionType());
        productCategories.add(getTransactionType1());
        productCategories.add(getTransactionType2());

        when(findTransactionCategoriesPort.findAll()).thenReturn(productCategories);

        List<TransactionType> result = findTransactionCategoriesPort.findAll();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testUpdateTransactionType() {
        UUID id = UUID.randomUUID();
        TransactionType productCategory = getTransactionType();
        productCategory.setName("Updated Name");

        when(updateTransactionTypePort.update(id, productCategory)).thenReturn(productCategory);

        TransactionType result = updateTransactionTypePort.update(id, productCategory);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
    }

}*/
