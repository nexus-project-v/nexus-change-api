/*package br.com.nexus.transaction.factory;

import br.com.nexus.transaction.core.domain.Transaction;
import br.com.nexus.transaction.core.domain.TransactionType;
import br.com.nexus.transaction.infrastructure.entity.transaction.TransactionEntity;
import br.com.nexus.transaction.infrastructure.entity.transactiontype.TransactionTypeEntity;

import java.math.BigDecimal;
import java.util.UUID;

public class ObjectFactory {
    public static ObjectFactory instance;

    private ObjectFactory() {}

    public static ObjectFactory getInstance() {
        if (instance == null) {
            instance = new ObjectFactory();
        }
        return instance;
    }

    public TransactionEntity getTransactionEntity(TransactionTypeEntity transactionTypeEntity) {
        return TransactionEntity.builder()
                .name("Coca-Cola")
                .code(UUID.randomUUID().toString())
                .pic("hhh")
                .price(BigDecimal.TEN)
                .description("Coca-Cola")
                .transactionType(transactionTypeEntity)
                .build();
    }

    public TransactionEntity getTransactionEntity1(TransactionTypeEntity transactionTypeEntity) {
        return TransactionEntity.builder()
                .name("Bebida 1")
                .code(UUID.randomUUID().toString())
                .pic("hhh")
                .price(BigDecimal.TEN)
                .description("Coca-Cola")
                .transactionType(transactionTypeEntity)
                .build();
    }

    public TransactionEntity getTransactionEntity2(TransactionTypeEntity transactionTypeEntity) {
        return TransactionEntity.builder()
                .name("Bebida 2")
                .code(UUID.randomUUID().toString())
                .pic("hhh")
                .price(BigDecimal.TEN)
                .description("Coca-Cola")
                .transactionType(transactionTypeEntity)
                .build();
    }

    public Transaction getTransaction(TransactionType transactionTypeEntity) {
        return Transaction.builder()
                .name("Bebida")
                .code(UUID.randomUUID().toString())
                .pic("hhh")
                .price(BigDecimal.TEN)
                .description("Coca-Cola")
                .transactionTypeId(transactionTypeEntity.getId())
                .build();
    }

    public Transaction getTransaction1(TransactionType transactionType) {
        return Transaction.builder()
                .name("Bebida 1")
                .code(UUID.randomUUID().toString())
                .pic("hhh")
                .price(BigDecimal.TEN)
                .description("Coca-Cola")
                .transactionTypeId(transactionType.getId())
                .build();
    }

    public Transaction getTransaction2(TransactionType transactionType) {
        return Transaction.builder()
                .name("Bebida 2")
                .code(UUID.randomUUID().toString())
                .pic("hhh")
                .price(BigDecimal.TEN)
                .description("Coca-Cola")
                .transactionTypeId(transactionType.getId())
                .build();
    }


    public TransactionTypeEntity getTransactionTypeEntity() {
        return TransactionTypeEntity.builder()
                .name("Bebida")
                .build();
    }

    public TransactionType getTransactionType() {
        return TransactionType.builder()
                .name("Bebida")
                .build();
    }

    public TransactionType getTransactionTypeTo260() {
        return TransactionType.builder()
                .name("a".repeat(260))
                .build();
    }
}*/