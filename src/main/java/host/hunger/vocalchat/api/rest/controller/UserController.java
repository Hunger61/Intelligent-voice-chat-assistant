package host.hunger.vocalchat.api.rest.controller;

import host.hunger.vocalchat.infrastructure.annotation.AutoResult;
import host.hunger.vocalchat.api.rest.dto.UserLoginDTO;
import host.hunger.vocalchat.api.rest.dto.UserRegisterDTO;
import host.hunger.vocalchat.application.service.UserRegistrationApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/public/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRegistrationApplicationService userRegistrationApplicationService;

    @PostMapping("/register")
    @AutoResult
    public void register(@RequestBody UserRegisterDTO userRegisterDTO) {
        userRegistrationApplicationService.registerUser(userRegisterDTO.getUsername(), userRegisterDTO.getPassword(), userRegisterDTO.getEmail());
    }

    @PostMapping("/login")
    @AutoResult
    public void login(@RequestBody UserLoginDTO userLoginDTO) {

    }
}
