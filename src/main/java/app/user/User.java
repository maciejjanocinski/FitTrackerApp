package app.user;

import app.bodymetrics.BodyMetrics;
import app.diary.Diary;
import app.ingredient.Ingredient;
import app.recipe.Recipe;
import app.roles.Role;
import app.stripe.StripeCustomer;
import app.user.dto.UpdateProfileInfoDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static app.util.Utils.LASTLY_ADDED_PRODUCTS_LIMIT;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String name;
    private String surname;

    @Column(unique = true)
    private String email;
    private String phone;
    private String password;

    @OneToOne(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "user")
    private StripeCustomer stripeCustomer;

    @OneToOne(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "user")
    private BodyMetrics bodyMetrics;

    @ManyToMany
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> authorities;

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "user")
    private List<Diary> diariesHistory;

    @OneToOne(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private Diary diary;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Recipe> lastlySearchedRecipes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Ingredient> lastlySearchedIngredients;

    @OneToMany(cascade = CascadeType.ALL)
    @OrderBy("id DESC")
    private List<Ingredient> lastlyAddedIngredients;

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
    public void removeRole(Role role) {
        authorities.remove(role);
    }

    public int getYears() {
        return Period.between(bodyMetrics.getBirthDate(), LocalDate.now()).getYears();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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



    void updateUserProfile(UpdateProfileInfoDto updateProfileInfoDto) {
        this.setUsername(updateProfileInfoDto.username());
        this.setName(updateProfileInfoDto.name());
        this.setSurname(updateProfileInfoDto.surname());
        this.setEmail(updateProfileInfoDto.email());
        this.setPhone(updateProfileInfoDto.phone());
    }

   public void updateLastlyAddedProducts(Ingredient ingredient) {
        if (lastlyAddedIngredients.size() == LASTLY_ADDED_PRODUCTS_LIMIT) {
            lastlyAddedIngredients.get(lastlyAddedIngredients.size() - 1).setLastlyAdded(false);
            lastlyAddedIngredients.remove(lastlyAddedIngredients.size() - 1);
        }
        lastlyAddedIngredients.add(ingredient);
    }

}
