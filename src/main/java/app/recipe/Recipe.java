package app.recipe;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String label;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String image;
    private String source;
    private String url;
    private int yield;
    @ElementCollection
    private List<String> ingredientLines;
    private double caloriesPerServing;
    private double proteinPerServing;
    private double carbsPerServing;
    private double fatPerServing;
    private double fiberPerServing;
    private boolean isUsed;
    private String query;
}
