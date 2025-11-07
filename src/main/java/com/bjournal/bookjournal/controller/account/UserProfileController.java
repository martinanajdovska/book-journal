package com.bjournal.bookjournal.controller.account;

import com.bjournal.bookjournal.model.User;
import com.bjournal.bookjournal.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserProfileController {
    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String getProfilePage(Model model, @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        Optional<User> optionalUser = userService.findByUsername(username);
        if (!optionalUser.isPresent()) {
            model.addAttribute("error", "User doesn't exist");
            return "error-page";
        }
        model.addAttribute("user", optionalUser.get());
        return "/account/profile";
    }
}
