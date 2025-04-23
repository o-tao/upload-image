package example.domain.images;

import example.domain.BaseEntity;
import example.domain.products.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Image extends BaseEntity {

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String uploadToken;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

}
