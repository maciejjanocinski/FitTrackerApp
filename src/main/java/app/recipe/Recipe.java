package app.recipe;

import app.diary.Diary;
import app.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String label;
    @Column(columnDefinition = "TEXT")
    private String image;
    private String source;
    private String url;
    private int yield;

    private BigDecimal caloriesPerServing;
    private BigDecimal proteinPerServing;
    private BigDecimal carbsPerServing;
    private BigDecimal fatPerServing;
    private BigDecimal fiberPerServing;
    private String query;

    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JsonManagedReference
    private List<IngredientLine> ingredientLines;

    @ManyToOne
    @JsonBackReference
    private User user;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "diary")
    private Diary diary;
}
