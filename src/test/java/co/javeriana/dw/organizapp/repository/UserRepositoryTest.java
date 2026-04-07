package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.User;
import co.javeriana.dw.thymeleaf.ThymeleafApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ThymeleafApplication.class)
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void existsByEmailShouldReturnTrueWhenUserExists() {
        buildUser("test@mail.com");

        boolean exists = userRepository.existsByEmail("test@mail.com");

        assertTrue(exists);
    }

    @Test
    void existsByEmailShouldReturnFalseWhenUserDoesNotExist() {
        boolean exists = userRepository.existsByEmail("ghost@mail.com");

        assertFalse(exists);
    }

    private User buildUser(String email) {
        Company company = buildCompany();

        User user = new User();
        user.setName("Test User");
        user.setEmail(email);
        user.setCompany(company);

        return userRepository.saveAndFlush(user);
    }
    private Company buildCompany() {
        Company company = new Company();
        company.setName("Company " + Math.random());
        company.setNit(String.valueOf(Math.random()));
        company.setIndustry("Tech");
        return companyRepository.save(company);
    }
}