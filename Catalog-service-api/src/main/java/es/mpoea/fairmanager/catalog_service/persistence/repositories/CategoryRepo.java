package es.mpoea.fairmanager.catalog_service.persistence.repositories;

import es.mpoea.fairmanager.catalog_service.persistence.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category, Long> {
}
