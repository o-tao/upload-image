package example.product.controller.dto;

import example.product.service.dto.ProductCreateInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCreateRequest {

    private String name;
    private int price;
    private String description;

    public ProductCreateRequest(String name, int price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public ProductCreateInfo toCreate() {
        return new ProductCreateInfo(name, price, description);
    }
}
