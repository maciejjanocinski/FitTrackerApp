package app.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ResponseDto {
    private String text;
    private List<ParsedDto> parsed;
    private List<HintDto> hints;

}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ParsedDto {
    private FoodDto food;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class HintDto {
    private FoodDto food;
    private List<MeasureResponseDto> measures;

}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class FoodDto {
    private String foodId;
    private String label;
    private String knownAs;
    private Map<String, BigDecimal> nutrients;
    private String category;
    private String categoryLabel;
    private String image;

}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class MeasureResponseDto {
    private String uri;
    private String label;
    private BigDecimal weight;

}
