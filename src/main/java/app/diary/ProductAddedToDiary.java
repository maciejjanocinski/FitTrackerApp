package app.diary;

import app.diary.Diary;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
 class ProductAddedToDiary {
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
    @JsonManagedReference
    private Diary diary;
}
