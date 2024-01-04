package app.bodyMetrics;

import app.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.UnexpectedTypeException;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Data
public class BodyMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Gender gender;
    private LocalDate birthDate;
    private Double height;
    private Double weight;
    private Double neck;
    private Double waist;
    private Double hip;

    @OneToOne
    @JsonBackReference
    private User user;

    public static Gender setGenderFromString(String gender) {
        if (Objects.equals(gender, "MALE")) {
            return Gender.MALE;
        } else if (Objects.equals(gender, "FEMALE")) {
            return Gender.FEMALE;
        }
        throw new UnexpectedTypeException("Gender doesn't match any of the values. Should be \"MALE\" or \"FEMALE\".");
    }

    public BodyMetrics() {
        gender = Gender.MALE;
        birthDate = LocalDate.now();
        height = 180.0;
        weight = 80.0;
        neck = 50.0;
        waist = 100.0;
        hip = 120.0;
    }
}
