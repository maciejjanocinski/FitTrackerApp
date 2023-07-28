package app.models;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email") )
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    private String password;

    private String role;

    public User(String name, String email, String password) {
        this.username = name;
        this.email = email;
        this.password = password;
    }

    public User() {

    }
}
