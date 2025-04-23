package example.product.service.dto;

import lombok.Getter;

@Getter
public class ProductCreateInfo {

    private final String name;
    private final int price;
    private final String description;

    public ProductCreateInfo(String name, int price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }
}
