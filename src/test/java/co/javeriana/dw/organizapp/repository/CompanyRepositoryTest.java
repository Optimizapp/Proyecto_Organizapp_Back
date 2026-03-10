package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.thymeleaf.ThymeleafApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = ThymeleafApplication.class)
@Transactional
class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void existsByNitShouldReturnTrueWhenCompanyExists() {
        companyRepository.save(buildCompany("Acme", "900123456", "Tech"));

        boolean exists = companyRepository.existsByNit("900123456");

        assertTrue(exists);
    }

    @Test
    void existsByNitShouldReturnFalseWhenCompanyDoesNotExist() {
        boolean exists = companyRepository.existsByNit("999999999");

        assertFalse(exists);
    }

    @Test
    void existsByNameShouldReturnTrueWhenCompanyExists() {
        companyRepository.save(buildCompany("Beta Corp", "900654321", "Finance"));

        boolean exists = companyRepository.existsByName("Beta Corp");

        assertTrue(exists);
    }

    @Test
    void existsByNameShouldReturnFalseWhenCompanyDoesNotExist() {
        boolean exists = companyRepository.existsByName("Ghost Company");

        assertFalse(exists);
    }

    private Company buildCompany(String name, String nit, String industry) {
        Company company = new Company();
        company.setName(name);
        company.setNit(nit);
        company.setIndustry(industry);
        return company;
    }
}
