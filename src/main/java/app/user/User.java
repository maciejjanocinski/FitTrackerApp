package app.user;

import app.roles.Role;
import app.bodymetrics.BodyMetrics;
import app.diary.Diary;
import app.product.Product;
import app.recipe.Recipe;
import app.stripe.StripeCustomer;
import app.user.dto.UpdateProfileInfoDto;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
            fetch = FetchType.EAGER,
            mappedBy = "user")
    private StripeCustomer stripeCustomer;

    @OneToOne(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "user")
    private BodyMetrics bodyMetrics;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> authorities;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Recipe> lastlySearchedRecipes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Product> lastlySearchedProducts;

    @OneToMany
    private List<Product> lastlyAddedProducts;

    private String lastProductQuery;
    private String lastRecipeQuery;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
    }

    public void removeProduct(Product product) {
        lastlySearchedProducts.remove(product);
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

    void updateBodyMetrics(BodyMetrics bodyMetrics) {
        this.setBodyMetrics(bodyMetrics);
    }

   public void updateLastlyAddedProducts(Product product) {
        if (lastlyAddedProducts.size() == 15) {
            lastlyAddedProducts.remove(0);
        }
        lastlyAddedProducts.add(product);
    }

}
