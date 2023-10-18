package app.product;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
 class ResponseDTO {
    private String text;
    private List<ParsedDTO> parsed;
    private List<HintDTO> hints;

}

@Data
 class ParsedDTO {
    private FoodDTO food;

}

@Data
 class HintDTO {
    private FoodDTO food;
    private List<MeasureDTO> measures;

}

@Data
 class FoodDTO {
    private String foodId;
    private String label;
    private String knownAs;
    private Map<String, BigDecimal> nutrients;
    private String category;
    private String categoryLabel;
    private String image;

}

@Data
 class MeasureDTO {
    private String uri;
    private String label;
    private BigDecimal weight;

}
