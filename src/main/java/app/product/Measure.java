package app.product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Measure {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;

    private BigDecimal weight;
}
