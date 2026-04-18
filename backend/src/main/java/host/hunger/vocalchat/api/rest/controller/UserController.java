package host.hunger.vocalchat.api.rest.controller;

import host.hunger.vocalchat.api.rest.annotation.AutoResult;
import host.hunger.vocalchat.api.rest.annotation.OperateLog;
import host.hunger.vocalchat.api.rest.annotation.SkipToken;
import host.hunger.vocalchat.api.rest.dto.UserLoginDTO;
import host.hunger.vocalchat.api.rest.dto.UserRegisterDTO;
import host.hunger.vocalchat.api.rest.vo.UserInfoVO;
import host.hunger.vocalchat.application.service.UserApplicationService;
import host.hunger.vocalchat.domain.model.user.User;
import host.hunger.vocalchat.shared.context.UserContext;
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
    @OperateLog("用户注册")
    @SkipToken
    public String register(@RequestBody UserRegisterDTO userRegisterDTO) {
        return userApplicationService.registerUser(userRegisterDTO.getNickName(), userRegisterDTO.getPassword(), userRegisterDTO.getEmail(), userRegisterDTO.getVerificationCode());
    }

    @PostMapping("/login")
    @AutoResult
    @OperateLog("用户登录")
    @SkipToken
    public String login(@RequestBody UserLoginDTO userLoginDTO) {
        return userApplicationService.login(userLoginDTO.getEmail(), userLoginDTO.getPassword());
    }

    @PostMapping("/getVerificationCode")
    @AutoResult
    @OperateLog("发送验证码")
    @SkipToken
    public void getVerificationCode(@RequestParam String email) {
        userApplicationService.sendVerificationCode(email);
    }// todo缺少返回值

    @PostMapping("/logout")
    @AutoResult
    @OperateLog("用户登出")
    public void logout() {
        User user = UserContext.require();
        userApplicationService.logout(user);
    }

    @GetMapping("/info")
    @AutoResult
    @OperateLog("查询用户信息")
    public UserInfoVO getUserInfo() {
        User user = UserContext.require();
        return new UserInfoVO(
                user.getId() == null ? null : user.getId().toString(),
                user.getNickName() == null ? null : user.getNickName().getNickName(),
                user.getEmail() == null ? null : user.getEmail().getEmail());
    }

}
