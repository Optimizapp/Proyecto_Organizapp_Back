package co.javeriana.dw.organizapp.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompanyTest {


    @Test
    void testCompanyCreation() {
        Company company = new Company();
        company.setId(1L);
        company.setName("Test Company");
        company.setNit("123456789");

        assertEquals(1L, company.getId());
        assertEquals("Test Company", company.getName());
        assertEquals("123456789", company.getNit());
    }

    @Test
    void testCompanyEqualsAndHashCode() {
        Company company1 = new Company();
        company1.setId(1L);
        company1.setNit("123456789");

        Company company2 = new Company();
        company2.setId(1L);
        company2.setNit("123456789");

        assertEquals(company1, company2);
        assertEquals(company1.hashCode(), company2.hashCode());
    }

    @Test
    void testCompanyToString() {
        Company company = new Company();
        company.setId(1L);
        company.setName("Test Company");
        
        String toString = company.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Test Company"));
    }

    @Test
    void shouldSetTimestampsOnPrePersist() {
        Company company = new Company();

        company.prePersist();

        assertNotNull(company.getCreatedAt());
        assertNotNull(company.getUpdatedAt());
    }

    @Test
    void shouldUpdateOnlyUpdatedAtOnPreUpdate() {
        Company company = new Company();
        company.prePersist();
        LocalDateTime createdAt = company.getCreatedAt();

        company.preUpdate();

        assertEquals(createdAt, company.getCreatedAt());
        assertNotNull(company.getUpdatedAt());
    }

    @Test
    void shouldAddAndRemoveUser() {
        Company company = new Company();
        User user = new User();

        company.addUser(user);

        assertTrue(company.getUsers().contains(user));
        assertEquals(company, user.getCompany());

        company.removeUser(user);

        assertFalse(company.getUsers().contains(user));
        assertNull(user.getCompany());
    }

    @Test
    void shouldAddAndRemoveProcess() {
        Company company = new Company();
        Process process = new Process();

        company.addProcess(process);

        assertTrue(company.getProcesses().contains(process));
        assertEquals(company, process.getCompany());

        company.removeProcess(process);

        assertFalse(company.getProcesses().contains(process));
        assertNull(process.getCompany());
    }
}
