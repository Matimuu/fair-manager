package es.mpoea.fairmanager.catalog_service.api.services;

import es.mpoea.fairmanager.catalog_service.api.commands.CreateProductCommand;
import es.mpoea.fairmanager.catalog_service.api.commands.UpdateProductCommand;
import es.mpoea.fairmanager.catalog_service.api.exceptions.CategoryNotExistsException;
import es.mpoea.fairmanager.catalog_service.api.exceptions.ProductNotFoundException;
import es.mpoea.fairmanager.catalog_service.api.exceptions.ProductsNotExistsException;
import es.mpoea.fairmanager.catalog_service.persistence.models.Category;
import es.mpoea.fairmanager.catalog_service.persistence.models.Product;
import es.mpoea.fairmanager.catalog_service.persistence.repositories.CategoryRepo;
import es.mpoea.fairmanager.catalog_service.persistence.repositories.ProductRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;

    @Transactional
    public Product createProduct(CreateProductCommand createCommand) {
        String categoryName = createCommand.categoryName();

        if (categoryName == null || categoryName.isBlank())
            return productRepo.save(new Product (createCommand.label(), createCommand.description(), null));

        Category category = categoryRepo.findByName(categoryName).orElseGet(() -> categoryRepo.save(new Category(categoryName.trim())));

        return productRepo.save(new Product(createCommand.label(), createCommand.description(), category));
    }

    public List<Product> getAllProducts() {
        List<Product> products = productRepo.findAll();
        if (products.isEmpty()) throw new ProductsNotExistsException();

        return products;
    }

    public Product getProductById(long productId) {
        return productRepo.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
    }

    @Transactional
    public Product updateProduct(long productId, UpdateProductCommand command) {
        Product product = productRepo.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));

        if (command.label() != null) product.setLabel(command.label());
        if (command.description() != null) product.setDescription(command.description());
        if (command.categoryName() != null) {
            if (command.categoryName().isBlank()) product.setCategory(null);
            else {
                Category category = categoryRepo.findByName(command.categoryName().trim())
                        .orElseGet(() -> categoryRepo.save(new Category(command.categoryName().trim())));
                product.setCategory(category);
            }
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
