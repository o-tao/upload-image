package example.product.service.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCreateInfo {

    private String name;
    private int price;
    private String description;

    public ProductCreateInfo(String name, int price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }
}
