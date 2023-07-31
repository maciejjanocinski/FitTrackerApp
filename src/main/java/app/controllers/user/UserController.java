package app.controllers.user;

import app.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    UserRepository userRepository;

    @GetMapping("/")
    public String user() {
        return "user";
    }


}
