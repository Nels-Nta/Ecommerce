package com.ntabana.backend.controller;

import com.ntabana.backend.dto.UserDto;
import com.ntabana.backend.product.Product;
import com.ntabana.backend.user.EmailAlreadyExistsException;
import com.ntabana.backend.user.User;
import com.ntabana.backend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("index")
    public String home(){
        return "index";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    // handler method to handle user registration request
    @GetMapping("register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    // handler method to handle register user form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto user,
                               BindingResult result,
                               Model model){
        User existing = userService.findByEmail(user.getEmail());
        if (existing != null) {
            throw new EmailAlreadyExistsException("There is already an account registered with that email");
        }
        System.out.println("In post mapping of auth COntroller");
        if (result.hasErrors()) {
            System.out.println("Some errors");
            model.addAttribute("user", user);
            return "register";
        }
        System.out.println("No errors");
        userService.saveUser(user);
        return "redirect:/register?success";
    }


@GetMapping("/admin/home")
public String listRegisteredUsers(Model model, @AuthenticationPrincipal UserDetails userDetails) {
    List<UserDto> users = userService.findAllUsers();
    model.addAttribute("users", users);


    // Get the authenticated user's name
    String username = userDetails.getUsername();
    model.addAttribute("currentUser", username);

    return "Home";
}

}