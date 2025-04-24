package example.domain.images;

import example.domain.BaseEntity;
import example.domain.products.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseEntity {

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String uploadToken;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    public Image(String url, String uploadToken, Product product) {
        this.url = url;
        this.uploadToken = uploadToken;
        this.product = product;
    }

    public static Image create(String url, String uploadToken, Product product) {
        return new Image(url, uploadToken, product);
    }
}
