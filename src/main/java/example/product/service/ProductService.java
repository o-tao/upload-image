package example.product.service;

import example.domain.products.Product;
import example.domain.products.ProductRepository;
import example.product.service.dto.ProductCreateInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public void createProduct(ProductCreateInfo productCreateInfo) {
        productRepository.save(
                Product.create(productCreateInfo.getName(),
                        productCreateInfo.getPrice(),
                        productCreateInfo.getDescription())
        );
    }
}
