package tn.enicar.enicar_platforme.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicar.enicar_platforme.user.User;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    // Method to fetch documents by user
    List<Document> findByUser(User user);
}
