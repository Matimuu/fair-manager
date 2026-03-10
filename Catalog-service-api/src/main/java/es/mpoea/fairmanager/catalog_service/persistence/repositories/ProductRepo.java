package es.mpoea.fairmanager.catalog_service.persistence.repositories;

import es.mpoea.fairmanager.catalog_service.persistence.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, Long> {
}
