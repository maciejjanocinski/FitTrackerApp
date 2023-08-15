package app.productAddedToDiary;

import app.diary.Diary;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ProductAddedToDiary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productId;
    private String productName;
    private double kcal;
    private double protein;
    private double carbohydrates;
    private double fat;
    private double fiber;
    private String image;
    private String measureLabel;
    private double quantity;


    @ManyToOne
    @JoinColumn(name = "diary_id")
    @JsonIgnoreProperties("products")
    private Diary diary;

    public ProductAddedToDiary(String productId,
                               String productName,
                               double kcal,
                               double protein,
                               double carbohydrates,
                               double fat,
                               double fiber,
                               String image,
                               String measureLabel,
                               double quantity,
                               Diary diary) {
        this.productId = productId;
        this.productName = productName;
        this.kcal = kcal;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
        this.fiber = fiber;
        this.image = image;
        this.measureLabel = measureLabel;
        this.quantity = quantity;
        this.diary = diary;
    }
}
