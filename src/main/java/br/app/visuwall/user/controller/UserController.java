package br.app.visuwall.user.controller;

import br.app.visuwall.shared.config.security.AuthUtils;
import br.app.visuwall.user.domain.User;
import br.app.visuwall.user.dto.UserResponse;
import br.app.visuwall.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        User user = userService.getById(AuthUtils.currentUserId(authentication));
        return UserResponse.from(user);
    }
}
