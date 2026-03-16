package es.mpoea.fairmanager.catalog_service.persistence.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")

@NoArgsConstructor

@Getter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_id_seq")
    @SequenceGenerator(
            name = "category_id_seq",
            sequenceName = "category_id_seq",
            allocationSize = 50
    )
    private Long id;

    @Column(name = "name",unique = true, length = 120, nullable = false)
    @Setter
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamptz")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamptz")
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private final List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        if (product == null) return;

        product.setCategory(this);
    }

    public void removeProduct(Product product) {
        if (product == null) return;

        if (product.getCategory() == this) product.setCategory(null);
    }

    public Category(String name) {
        this.name = name;
    }
}
