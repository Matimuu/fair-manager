package es.mpoea.fairmanager.product_service.api.services;

import es.mpoea.fairmanager.product_service.api.commands.CreateProductCommand;
import es.mpoea.fairmanager.product_service.api.commands.UpdateProductCommand;
import es.mpoea.fairmanager.product_service.api.exceptions.ProductNotFoundException;
import es.mpoea.fairmanager.product_service.api.exceptions.ProductsNotExistsException;
import es.mpoea.fairmanager.product_service.persistence.models.Product;
import es.mpoea.fairmanager.product_service.persistence.repositories.ProductRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepo productRepo;

    public Product createProduct(CreateProductCommand createCommand) {
        return productRepo.save(new Product(createCommand.label(), createCommand.description(), createCommand.category()));
    }

    public List<Product> getAllProducts() {
        List<Product> products = productRepo.findAll();
        if (products.isEmpty()) throw new ProductsNotExistsException();

        return products;
    }

    public Product getProductById(long productId) {
        Optional<Product> productOpt = productRepo.findById(productId);

        if (productOpt.isEmpty()) throw new ProductNotFoundException(productId);

        return productOpt.get();
    }

    @Transactional
    public Product updateProduct(long productId, UpdateProductCommand updateCommand) {
        Product product = productRepo.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));

        if (updateCommand.label() != null) product.setLabel(updateCommand.label());
        if (updateCommand.description() != null) product.setDescription(updateCommand.description());
        if (updateCommand.category() != null) product.setCategory(updateCommand.category());

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
