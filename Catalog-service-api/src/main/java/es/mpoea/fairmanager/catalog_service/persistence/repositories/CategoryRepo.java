package es.mpoea.fairmanager.catalog_service.persistence.repositories;

import es.mpoea.fairmanager.catalog_service.persistence.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepo extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    void deleteByName(String name);
}
