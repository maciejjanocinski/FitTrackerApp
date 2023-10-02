package app.diary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public
class ProductInDiary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productId;
    private String productName;
    private BigDecimal kcal;
    private BigDecimal protein;
    private BigDecimal carbohydrates;
    private BigDecimal fat;
    private BigDecimal fiber;
    private String image;
    private String measureLabel;
    private BigDecimal quantity;

    @ManyToOne
    @JoinColumn(name = "diary_id")
    @JsonManagedReference
    @JsonIgnore
    private Diary diary;
}
