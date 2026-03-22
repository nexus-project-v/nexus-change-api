package br.com.nexus.change.domain;

import br.com.nexus.change.commons.Constants;
import br.com.nexus.change.infrastructure.entity.base.AuditDomain;
import br.com.nexus.change.infrastructure.entity.base.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AuditDomainTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void testDefaultValues() {
        AuditDomain auditDomain = new AuditDomain();

        assertNotNull(auditDomain.getCreatedDate());
        assertEquals(Constants.CURRENT_USER, auditDomain.getCreateBy());
       // assertEquals(TransactionStatus.STARTED, auditDomain.getStatus());
    }

    @Disabled
    void testNotNullValidation() {
        AuditDomain auditDomain = new AuditDomain();
        auditDomain.setCreatedDate(null);
        auditDomain.setCreateBy(null);
        auditDomain.setStatus(null);

    }

    @Test
    void testLastModifiedFields() {
        AuditDomain auditDomain = new AuditDomain();
        LocalDateTime now = LocalDateTime.now();
        String user = "testUser";

        auditDomain.setLastModifiedDate(now);
        auditDomain.setLastModifiedBy(user);

        assertEquals(now, auditDomain.getLastModifiedDate());
        assertEquals(user, auditDomain.getLastModifiedBy());
    }

    @Test
    void testGettersAndSetters() {
        AuditDomain auditDomain = new AuditDomain();

        LocalDateTime now = LocalDateTime.now();
        auditDomain.setCreatedDate(now);
        auditDomain.setCreateBy("testUser");
        auditDomain.setLastModifiedDate(now);
        auditDomain.setLastModifiedBy("testModifier");
        auditDomain.setStatus(Status.INATIVO);

        assertEquals(now, auditDomain.getCreatedDate());
        assertEquals("testUser", auditDomain.getCreateBy());
        assertEquals(now, auditDomain.getLastModifiedDate());
        assertEquals("testModifier", auditDomain.getLastModifiedBy());
        assertEquals(Status.INATIVO, auditDomain.getStatus());
    }

    @Test
    void testEqualsAndHashCode() {
        AuditDomain auditDomain1 = new AuditDomain();
        auditDomain1.setCreateBy("user");
        auditDomain1.setCreatedDate(LocalDateTime.now());
        auditDomain1.setStatus(Status.INATIVO);

        AuditDomain auditDomain2 = new AuditDomain();
        auditDomain2.setCreateBy("user");
        auditDomain2.setCreatedDate(auditDomain1.getCreatedDate());
        auditDomain2.setStatus(Status.INATIVO);

        assertEquals(auditDomain1, auditDomain2);
        assertEquals(auditDomain1.hashCode(), auditDomain2.hashCode());
    }

    @Test
    void testToString() {
        AuditDomain auditDomain = new AuditDomain();
        auditDomain.setCreateBy("user");
        auditDomain.setCreatedDate(LocalDateTime.of(2024, 7, 31, 12, 0));
        auditDomain.setStatus(Status.ATIVO);

        String expectedString = "AuditDomain(createdDate=2024-07-31T12:00, createBy=user, lastModifiedDate=null, lastModifiedBy=null, status=ATIVO)";
        assertEquals(expectedString, auditDomain.toString());
    }
}
