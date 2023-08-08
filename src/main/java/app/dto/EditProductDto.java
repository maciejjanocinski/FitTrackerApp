package app.dto;

import lombok.Data;

@Data
public class EditProductDto {
    Long id;
    String measureLabel;
    Double quantity;
}
