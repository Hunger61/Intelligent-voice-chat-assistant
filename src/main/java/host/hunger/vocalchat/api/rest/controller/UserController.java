package host.hunger.vocalchat.api.rest.controller;

import host.hunger.vocalchat.api.rest.annotation.AutoResult;
import host.hunger.vocalchat.api.rest.annotation.SkipToken;
import host.hunger.vocalchat.api.rest.dto.UserLoginDTO;
import host.hunger.vocalchat.api.rest.dto.UserRegisterDTO;
import host.hunger.vocalchat.application.service.UserApplicationService;
import host.hunger.vocalchat.domain.model.user.User;
import host.hunger.vocalchat.infrastructure.interceptor.UserInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/public/user")
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;

    @PostMapping("/register")
    @AutoResult
    @SkipToken
    public String register(@RequestBody UserRegisterDTO userRegisterDTO) {
        return userApplicationService.registerUser(userRegisterDTO.getNickName(), userRegisterDTO.getPassword(), userRegisterDTO.getEmail(), userRegisterDTO.getVerificationCode());
    }

    @PostMapping("/login")
    @AutoResult
    @SkipToken
    public String login(@RequestBody UserLoginDTO userLoginDTO) {
        return userApplicationService.login(userLoginDTO.getEmail(), userLoginDTO.getPassword());
    }

    @PostMapping("/getVerificationCode")
    @AutoResult
    @SkipToken
    public void getVerificationCode(@RequestParam String email) {
        userApplicationService.sendVerificationCode(email);
    }// todo缺少返回值

    @PostMapping("/logout")
    @AutoResult
    public void logout() {
        User user = UserInterceptor.userHolder.get();
        userApplicationService.logout(user);
    }
}
