package app.dto;

import lombok.Data;

@Data
public class AddProductDto {
    String foodId;
    String name;
    String measureLabel;
    Double quantity;
}
