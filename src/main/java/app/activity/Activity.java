package app.activity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Activity {
    @Id
    private String id;
    private String activity;
    private String metValue;
    private String description;
    private int intensityLevel;
}