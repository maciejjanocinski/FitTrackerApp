package app.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "diaries")
public class Diary {

    @Id
    @PrimaryKeyJoinColumn
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nutrients_sum_id")
    private NutrientsSum nutrientsSum;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "goals_id")
    private Goals goals;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nutrients_left_to_reach_today_goals_id")
    private NutrientsLeftToReachTodayGoals nutrientsLeftToReachTodayGoals;

    @JsonIgnoreProperties("diary")
    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL)
    private List<ProductAddedToDiary> products;

}
