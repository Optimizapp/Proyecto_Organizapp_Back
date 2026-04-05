package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.*;
import co.javeriana.dw.organizapp.entity.Process;
import co.javeriana.dw.thymeleaf.ThymeleafApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ThymeleafApplication.class)
@Transactional
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ProcessVersionRepository processVersionRepository;

    @Autowired
        private ProcessRepository processRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;


    @Test
    void shouldReturnCommentsByVersionId() {
        ProcessVersion version = buildVersion();
        processVersionRepository.save(version);

        Comment c1 = buildComment(version);
        Comment c2 = buildComment(version);

        commentRepository.save(c1);
        commentRepository.save(c2);

        List<Comment> result = commentRepository.findByVersionId(version.getId());

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoCommentsForVersion() {
        ProcessVersion version = buildVersion();
        processVersionRepository.save(version);

        List<Comment> result = commentRepository.findByVersionId(version.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldNotReturnCommentsFromOtherVersions() {
        ProcessVersion version1 = buildVersion();
        ProcessVersion version2 = buildVersion();

        processVersionRepository.save(version1);
        processVersionRepository.save(version2);

        Comment c1 = buildComment(version1);
        commentRepository.save(c1);

        List<Comment> result = commentRepository.findByVersionId(version2.getId());

        assertTrue(result.isEmpty());
    }


    private Comment buildComment(ProcessVersion version) {
        Comment comment = new Comment();
        comment.setVersion(version);
        comment.setUser(buildUser());
        comment.setContenido("Comentario de prueba");
        return comment;
        }
    private ProcessVersion buildVersion() {
        User user = buildUser();
        Company company = buildCompany();

        Process process = new Process();
        process.setUser(user);
        process.setName("Test Process");
        process.setCompany(company);
        process = processRepository.save(process); 

        ProcessVersion version = new ProcessVersion();
        version.setNumeroVersion(1);
        version.setProceso(process);
        version.setCreadoPor(user);

        return processVersionRepository.save(version);
    }
    private User buildUser() {
    Company company = buildCompany();
    companyRepository.save(company);

    User user = new User();
    user.setName("Juan");
    user.setEmail("user" + Math.random() + "@test.com");
    user.setCompany(company);

    return userRepository.save(user);
}
    private Company buildCompany() {
        Company company = new Company();
        company.setName("Company " + Math.random());
        company.setNit(String.valueOf(System.nanoTime()));
        company.setIndustry("Tech");
        return companyRepository.save(company);
    }
}