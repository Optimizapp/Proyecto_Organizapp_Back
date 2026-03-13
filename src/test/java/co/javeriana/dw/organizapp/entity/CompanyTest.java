package co.javeriana.dw.organizapp.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class CompanyTest {

    private ValidatorFactory validatorFactory;
    private Validator validator;

    @BeforeEach
    void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterEach
    void tearDown() {
        validatorFactory.close();
    }

    @Test
    void prePersistShouldSetCreatedAtAndUpdatedAt() {
        Company company = new Company();

        company.prePersist();

        assertNotNull(company.getCreatedAt());
        assertNotNull(company.getUpdatedAt());
    }

    @Test
    void preUpdateShouldRefreshUpdatedAt() {
        Company company = new Company();
        company.setUpdatedAt(LocalDateTime.now().minusDays(1));
        LocalDateTime originalUpdatedAt = company.getUpdatedAt();

        company.preUpdate();

        assertTrue(company.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void validateShouldFailWhenNameAndNitAreBlank() {
        Company company = new Company();
        company.setName("");
        company.setNit("");
        company.setIndustry("health");

        Set<ConstraintViolation<Company>> violations = validator.validate(company);

        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
    }

    @Test
    void validateShouldPassWhenCompanyIsValid() {
        Company company = new Company();
        company.setName("Gamma");
        company.setNit("900300300");
        company.setIndustry("Health");

        Set<ConstraintViolation<Company>> violations = validator.validate(company);

        assertTrue(violations.isEmpty());
    }
}
