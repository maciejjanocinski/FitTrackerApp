package app.controllers;

import app.services.RegisterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String SendRegister(@RequestParam("name") String name,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password) {

        registerService.register(name, email, password);

        return "register-success";
    }

}
