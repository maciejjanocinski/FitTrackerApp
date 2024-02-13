package app.common;

import app.diary.Diary;
import app.ingredient.Ingredient;
import app.nutrients.Nutrients;
import app.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String image;

    private String query;

    @OneToOne(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    private Nutrients nutrients;

    @ManyToOne
    private User user;

    @ManyToOne
    private Diary diary;

    public Product(Ingredient ingredient) {
        this.name = ingredient.getName();
        this.image = ingredient.getImage();
        this.query = ingredient.getQuery();
        this.nutrients = new Nutrients(ingredient.getNutrients());
        this.user = ingredient.getUser();
        this.diary = ingredient.getDiary();
    }

    public Product(Long id, String name, String image, String query, Nutrients nutrients) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.query = query;
        this.nutrients = nutrients;
    }
}
