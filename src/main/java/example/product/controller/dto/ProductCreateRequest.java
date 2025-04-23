package example.product.controller.dto;

import example.product.service.dto.ProductCreateInfo;
import lombok.Getter;

@Getter
public class ProductCreateRequest {

    private final String name;
    private final int price;
    private final String description;

    public ProductCreateRequest(String name, int price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public ProductCreateInfo toCreate() {
        return new ProductCreateInfo(name, price, description);
    }
}
