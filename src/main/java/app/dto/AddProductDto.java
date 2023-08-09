package app.dto;


public record AddProductDto(String foodId,
                            String name,
                            String measureLabel,
                            Double quantity) {

}
