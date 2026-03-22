/*package br.com.nexus.transaction.service;

import br.com.nexus.transaction.application.database.mapper.TransactionMapper;
import br.com.nexus.transaction.core.domain.Transaction;
import br.com.nexus.transaction.core.domain.TransactionType;
import br.com.nexus.transaction.core.ports.in.transaction.*;
import br.com.nexus.transaction.core.ports.out.TransactionRepositoryPort;
import br.com.nexus.transaction.core.service.TransactionService;
import br.com.nexus.transaction.infrastructure.entity.transaction.TransactionEntity;
import br.com.nexus.transaction.infrastructure.entity.transactiontype.TransactionTypeEntity;
import br.com.nexus.transaction.infrastructure.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.DataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    TransactionService transactionService;

    @Mock
    TransactionRepositoryPort transactionRepository;

    @Mock
    TransactionRepository repository;

    @Mock
    TransactionMapper mapper;

    @Mock
    CreateTransactionPort createTransactionPort;

    @Mock
    DeleteTransactionPort deleteTransactionPort;

    @Mock
    FindByIdTransactionPort findByIdTransactionPort;

    @Mock
    FindTransactionsPort findTransactionsPort;

    @Mock
    UpdateTransactionPort updateTransactionPort;

    private TransactionEntity getTransactionEntity(TransactionTypeEntity transactionTypeEntity) {
        return TransactionEntity.builder()
                .name("Bebida")
                .code(UUID.randomUUID().toString())
                .pic("hhh")
                .price(BigDecimal.TEN)
                .description("Coca-Cola")
                .transactionType(transactionTypeEntity)
                .build();
    }

    private TransactionEntity getTransactionEntity1(TransactionTypeEntity transactionTypeEntity) {
        return TransactionEntity.builder()
                .name("Bebida 1")
                .code(UUID.randomUUID().toString())
                .pic("hhh")
                .price(BigDecimal.TEN)
                .description("Coca-Cola")
                .transactionType(transactionTypeEntity)
                .build();
    }

    private TransactionEntity getTransactionEntity2(TransactionTypeEntity transactionTypeEntity) {
        return TransactionEntity.builder()
                .name("Bebida 2")
                .code(UUID.randomUUID().toString())
                .pic("hhh")
                .price(BigDecimal.TEN)
                .description("Coca-Cola")
                .transactionType(transactionTypeEntity)
                .build();
    }

    private Transaction getTransaction(TransactionType transactionTypeEntity) {
        return Transaction.builder()
                .name("Coca-Cola")
                .code(UUID.randomUUID().toString())
                .pic("hhh")
                .price(BigDecimal.TEN)
                .description("Coca-Cola")
                .transactionTypeId(transactionTypeEntity.getId())
                .build();
    }

    private Transaction getTransaction1(TransactionType transactionTypeEntity) {
        return Transaction.builder()
                .name("Bebida 1")
                .code(UUID.randomUUID().toString())
                .pic("hhh")
                .price(BigDecimal.TEN)
                .description("Coca-Cola")
                .transactionTypeId(transactionTypeEntity.getId())
                .build();
    }

    private Transaction getTransaction2(TransactionType transactionTypeEntity) {
        return Transaction.builder()
                .name("Bebida 2")
                .code(UUID.randomUUID().toString())
                .pic("hhh")
                .price(BigDecimal.TEN)
                .description("Coca-Cola")
                .transactionTypeId(transactionTypeEntity.getId())
                .build();
    }


    private TransactionTypeEntity getTransactionTypeEntity() {
        return TransactionTypeEntity.builder()
                .name("Bebida")
                .build();
    }

    private TransactionType getTransactionType() {
        return TransactionType.builder()
                .name("Bebida")
                .build();
    }

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getAllTransactionsTest() {
        List<Transaction> transactions = new ArrayList<>();
        List<TransactionEntity> listEntity = new ArrayList<>();

        Transaction client = getTransaction(getTransactionType());
        Transaction client1 = getTransaction1(getTransactionType());
        Transaction client2 = getTransaction2(getTransactionType());

        TransactionEntity clientEntity = getTransactionEntity(getTransactionTypeEntity());
        TransactionEntity clientEntity1 = getTransactionEntity1(getTransactionTypeEntity());
        TransactionEntity clientEntity2 = getTransactionEntity2(getTransactionTypeEntity());

        transactions.add(client);
        transactions.add(client1);
        transactions.add(client2);

        listEntity.add(clientEntity);
        listEntity.add(clientEntity1);
        listEntity.add(clientEntity2);

        when(transactionService.findAll()).thenReturn(transactions);

        List<Transaction> transactionList = transactionService.findAll();

        assertNotNull(transactionList);
    }

    @Test
    void getTransactionByIdTest() {
        Transaction transaction1 = getTransaction(getTransactionType());
        when(transactionService.findById(1L)).thenReturn(transaction1);

        Transaction transaction = transactionService.findById(1L);

        assertEquals("Coca-Cola", transaction.getName());
    }

    @Test
    void getFindTransactionByShortIdTest() {
        Transaction transaction = getTransaction(getTransactionType());
        when(transactionService.findById(1L)).thenReturn(transaction);

        Transaction result = transactionService.findById(1L);

        assertEquals("Coca-Cola", result.getName());
    }

    @Test
    void createTransactionTest() {
        Transaction transaction = getTransaction(getTransactionType());
        Transaction transactionResult = getTransaction(getTransactionType());
        transactionResult.setId(1L);

        when(transactionService.save(transaction)).thenReturn(transactionResult);
        Transaction save = transactionService.save(transaction);

        assertNotNull(save);
        //verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void testSaveRestaurantWithLongName() {
        Transaction transaction = new Transaction();
        transaction.setName("a".repeat(260)); // Nome com 260 caracteres, excedendo o limite de 255
        transaction.setCode(UUID.randomUUID().toString());
        transaction.setPic("hhh");
        transaction.setPrice(BigDecimal.TEN);
        transaction.setDescription("Coca-Cola");
        transaction.setTransactionTypeId(1L);
       
        // Simulando o lançamento de uma exceção
        doThrow(new DataException("Value too long for column 'name'", null)).when(transactionRepository).save(transaction);

        assertThrows(DataException.class, () -> {
            transactionRepository.save(transaction);
        });
    }

    @Test
    void testRemove_Exception() {
        Long transactionId = 99L;

        boolean result = transactionService.remove(transactionId);
        assertFalse(result);
        verify(transactionRepository, never()).remove(transactionId);
    }

    @Test
    void testCreateTransaction() {
        Transaction transaction = getTransaction(getTransactionType());
        Transaction transactionResult = getTransaction(getTransactionType());
        when(createTransactionPort.save(transaction)).thenReturn(transactionResult);

        Transaction result = createTransactionPort.save(transaction);

        assertNotNull(result);
        assertEquals("Coca-Cola", result.getName());
    }

    @Test
    void testDeleteTransaction() {
        Long transactionId = 1L;
        when(deleteTransactionPort.remove(transactionId)).thenReturn(true);

        boolean result = deleteTransactionPort.remove(transactionId);

        assertTrue(result);
    }

    @Test
    void testFindByIdTransaction() {
        Transaction transaction = getTransaction(getTransactionType());
        when(findByIdTransactionPort.findById(1L)).thenReturn(transaction);

        Transaction result = findByIdTransactionPort.findById(1L);

        assertNotNull(result);
        assertEquals("Coca-Cola", result.getName());
    }

    @Test
    void testFindTransactions() {
        Transaction transaction = getTransaction(getTransactionType());
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        when(findTransactionsPort.findAll()).thenReturn(transactions);
        List<Transaction> result = findTransactionsPort.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testUpdateTransaction() {
        Long transactionId = 1L;
        Transaction transaction = getTransaction(getTransactionType());

        when(updateTransactionPort.update(transactionId, transaction)).thenReturn(transaction);
        Transaction result = updateTransactionPort.update(transactionId, transaction);

        assertNotNull(result);
        assertEquals("Coca-Cola", result.getName());
    }
}*/