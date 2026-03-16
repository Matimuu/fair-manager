package es.mpoea.fairmanager.catalog_service.persistence.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "products")
@NoArgsConstructor
@Getter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_id_seq")
    @SequenceGenerator(
            name = "product_id_seq",
            sequenceName = "product_id_seq",
            allocationSize = 50
    )
    private Long id;

    @Column(name = "sku", nullable = false, unique = true, columnDefinition = "uuid", updatable = false)
    private UUID sku;

    @Column(name = "label", nullable = false, length = 240, unique = true)
    @Setter
    private String label;

    @Column(name = "description", columnDefinition = "text")
    @Setter
    private String description;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamptz")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamptz")
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public Product(String label, String description, Category category) {
        this.sku = UUID.randomUUID();

        this.label = label;
        this.description = description;
        setCategory(category);
    }

//  Method to maintain bidirectional relationship
    public void setCategory(Category newCategory) {
        if (Objects.equals(this.category, newCategory)) return;

        if (this.category != null) {
            this.category.getProducts().remove(this);
        }

        this.category = newCategory;

        if (newCategory != null && !newCategory.getProducts().contains(this)) {
            newCategory.getProducts().add(this);
        }
    }
}
