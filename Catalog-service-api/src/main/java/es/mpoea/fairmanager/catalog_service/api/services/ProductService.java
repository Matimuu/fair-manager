package es.mpoea.fairmanager.catalog_service.api.services;

import es.mpoea.fairmanager.catalog_service.api.commands.product.CreateProductCommand;
import es.mpoea.fairmanager.catalog_service.api.commands.product.UpdateProductCommand;
import es.mpoea.fairmanager.catalog_service.api.exceptions.product.ProductAlreadyExistsException;
import es.mpoea.fairmanager.catalog_service.api.exceptions.product.ProductNotFoundException;
import es.mpoea.fairmanager.catalog_service.persistence.models.Category;
import es.mpoea.fairmanager.catalog_service.persistence.models.Product;
import es.mpoea.fairmanager.catalog_service.persistence.repositories.ProductRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepo productRepo;
    private final CategoryService categoryService;

    @Transactional
    public Product createProduct(CreateProductCommand createCommand) {
        String label = createCommand.label() == null ? null : createCommand.label().trim();

        if (label == null || label.isBlank())
            throw new IllegalArgumentException("Product label cannot be null or blank");

        if (productRepo.existsByLabel(label))
            throw new ProductAlreadyExistsException(label);

        String categoryName = createCommand.categoryName() == null ? null : createCommand.categoryName().trim();

        if (categoryName == null || categoryName.isBlank())
            return productRepo.save(
                    new Product(label, createCommand.description(), null)
            );

        Category category = categoryService.getOrCreateCategory(categoryName);

        return productRepo.save(
                new Product(label, createCommand.description(), category)
        );
    }

    public List<Product> getAllProducts() {
        return productRepo.findAllWithCategory();
    }

    public Product getProductById(long productId) {
        return productRepo.findByIdWithCategory(productId).orElseThrow(() -> new ProductNotFoundException(productId));
    }

    @Transactional
    public Product updateProduct(long productId, UpdateProductCommand command) {
        Product product = productRepo
                .findByIdWithCategory(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        String newLabel = command.label() == null ? null : command.label().trim();

        if (newLabel != null) {
            if (newLabel.isBlank()) throw new IllegalArgumentException("New label cannot be null or blank");
            productRepo.findByLabel(newLabel)
                    .filter(prod -> prod.getId() != productId)
                    .ifPresent(found -> {
                        throw new ProductAlreadyExistsException(newLabel);
                    });

            product.setLabel(newLabel);
        }

        if (command.description() != null)
            product.setDescription(command.description());

        String newCategoryName = command.categoryName() == null ? null : command.categoryName().trim();

        if (newCategoryName != null) {
            if (newCategoryName.isBlank())
                product.setCategory(null);
            else
                product.setCategory(categoryService.getOrCreateCategory(newCategoryName));
        }

        return productRepo.save(product);
    }

    @Transactional
    public void deleteProduct(long id) {
        if (!productRepo.existsById(id)) {
            throw new ProductNotFoundException(id);
        }

        productRepo.deleteById(id);
    }
}
