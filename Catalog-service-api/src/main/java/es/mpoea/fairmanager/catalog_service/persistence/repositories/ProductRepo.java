package es.mpoea.fairmanager.catalog_service.persistence.repositories;

import es.mpoea.fairmanager.catalog_service.persistence.models.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepo extends JpaRepository<Product, Long> {
    Optional<Product> findByLabel(String label);
    boolean existsByLabel(String label);

    @EntityGraph(attributePaths = "category")
    @Query("select p from Product p")
    List<Product> findAllWithCategory();

    @EntityGraph(attributePaths = "category")
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Product p set p.category = null where p.category.id = :categoryId")
    int clearCategoryByCategoryId(@Param("categoryId") Long categoryId);
}
