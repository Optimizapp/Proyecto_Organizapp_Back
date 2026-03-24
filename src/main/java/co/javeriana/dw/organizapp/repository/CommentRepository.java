package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByVersionId(Long versionId);
}
