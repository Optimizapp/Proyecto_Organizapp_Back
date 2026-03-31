package co.javeriana.dw.organizapp.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
}
