package app.user;

import app.authentication.Role;
import app.diary.Diary;
import app.diary.Gender;
import app.recipe.Recipe;
import jakarta.persistence.*;
import jakarta.validation.UnexpectedTypeException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Size(min = 6, message = "Username must have at least 6 characters.")
    @NotBlank(message = "Username cannot be blank.")
    private String username;

    @NotEmpty(message = "You have to pass your name.")
    @NotBlank(message = "Name cannot be blank.")
    private String name;

    @NotEmpty(message = "You have to pass your surname.")
    @NotBlank(message = "Surname cannot be blank.")
    private String surname;

    private Gender gender;

    @Email(message = "Wrong email")
    @NotEmpty(message = "You have to pass your email.")
    @NotBlank(message = "Email cannot be blank.")
    private String email;

    @Size(min = 9, max = 9, message = "Phone number must contain 9 digits.")
    @NotBlank(message = "Phone cannot be blank.")
    private String phone;

    @NotBlank(message = "Password cannot be blank.")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> authorities;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_favourite_recipes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id"))
    private List<Recipe> favouriteRecipes;

    private String lastProductQuery;
    private String lastRecipeQuery;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
    }

    public void addRole(Role role) {
        authorities.add(role);
    }
    public void addFavouriteRecipe(Recipe recipe) {
        favouriteRecipes.add(recipe);
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

   public static Gender setGenderFromString(String gender) {
        if(Objects.equals(gender, "MALE")) {
            return Gender.MALE;
        } else if (Objects.equals(gender, "FEMALE")) {
            return Gender.FEMALE;
        }
        throw new UnexpectedTypeException("Gender doesn't match any of the values. Should be \"MALE\" or \"FEMALE\".");
    }
}
