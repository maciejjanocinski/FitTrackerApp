package app.dto;

import lombok.Data;

@Data
public class EditProductDto {
    Long usersProductsId;
    String measureLabel;
    Double quantity;
}
