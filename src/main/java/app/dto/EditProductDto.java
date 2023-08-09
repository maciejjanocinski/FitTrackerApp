package app.dto;

import lombok.Data;

@Data
public class EditProductDto {
    private long id;
    private String measureLabel;
    private double quantity;
}
