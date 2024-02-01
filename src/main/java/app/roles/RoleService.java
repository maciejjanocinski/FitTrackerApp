package app.roles;

import app.exceptions.InvalidInputException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static app.util.Utils.ROLE_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

   public Role getRole(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new InvalidInputException(ROLE_NOT_FOUND_MESSAGE));
    }


}
