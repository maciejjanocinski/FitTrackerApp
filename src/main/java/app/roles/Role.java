package app.roles;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public enum roleType {
        ROLE_USER_STANDARD,
        ROLE_USER_PREMIUM,
        ROLE_ADMIN
    }

    @Override
    public String getAuthority() {
        return name;
    }
}
