package app.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class SearchResult {
    private int from;
    private int to;
    private int count;
    private Links _links;
    private List<RecipeAndLinkDto> hits;

}
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class RecipeAndLinkDto {
    private RecipeDto recipe;
}





@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
 class Nutrient {
    private String label;
    private double quantity;
    private String unit;
}
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
 class Links {
    private Link next;
}
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
 class Link {
    private String title;
    private String href;
}
