package app.dto;

import lombok.Data;

@Data
public class AddProductDto {
    private String foodId;
    private String name;
    private String measureLabel;
    private Double quantity;
}
