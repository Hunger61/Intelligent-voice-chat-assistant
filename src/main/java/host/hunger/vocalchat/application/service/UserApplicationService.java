package host.hunger.vocalchat.application.service;

import host.hunger.vocalchat.domain.factory.UserFactory;
import host.hunger.vocalchat.domain.model.user.NickName;
import host.hunger.vocalchat.domain.model.user.User;
import host.hunger.vocalchat.domain.model.user.UserEmail;
import host.hunger.vocalchat.domain.model.user.UserPassword;
import host.hunger.vocalchat.domain.repository.UserRepository;
import host.hunger.vocalchat.domain.service.UserDomainService;
import host.hunger.vocalchat.infrastructure.external.email.EmailService;
import host.hunger.vocalchat.infrastructure.util.JwtUtil;
import host.hunger.vocalchat.infrastructure.util.RedisKey;
import host.hunger.vocalchat.infrastructure.util.RedisUtil;
import host.hunger.vocalchat.infrastructure.exception.BaseException;
import host.hunger.vocalchat.infrastructure.Enum.ErrorEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;
    private final UserDomainService userDomainService;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final EmailService emailService;

    /**
     * 注册用户
     *
     * @param nickname         昵称
     * @param password         密码
     * @param email            邮箱
     * @param verificationCode 验证码
     * @return token 令牌
     */
    @Transactional
    public String registerUser(String nickname, String password, String email, String verificationCode) {
        UserEmail userEmail = new UserEmail(email);
        UserPassword userPassword = new UserPassword(password);
        NickName nickName = new NickName(nickname);
        String storedCode = (String) redisUtil.get(RedisKey.verificationCodeKey(userEmail));
        if (storedCode == null || !storedCode.equals(verificationCode)) {
            throw new BaseException(ErrorEnum.VERIFICATION_CODE_INCORRECT);
        }//todo 将storedCode直接判断是否存在
        redisUtil.delete(RedisKey.verificationCodeKey(userEmail));
        if (userRepository.existsByEmail(userEmail)) {
            throw new BaseException(ErrorEnum.EMAIL_ALREADY_USED);
        }
        User user = UserFactory.createWithGeneratedId(nickName, userEmail, userPassword);
        userDomainService.addDefaultAIAssistants(user.getId());
        userRepository.save(user);
        String token = jwtUtil.createToken(user.getId().toString());
        redisUtil.set(RedisKey.userKey(user.getId()), "", 86400);//todo
        return token;
    }

    /**
     * 登录用户
     *
     * @param email    邮箱
     * @param password 密码
     * @return token 令牌
     */
    public String login(String email, String password) {
        UserEmail userEmail = new UserEmail(email);
        UserPassword userPassword = new UserPassword(password);
        User user = userDomainService.authenticate(userEmail, userPassword);
        String token = jwtUtil.createToken(user.getId().toString());
        redisUtil.set(RedisKey.userKey(user.getId()), "", 86400);//todo
        return token;
    }

    /**
     * 获取验证码
     *
     * @param email 邮箱
     */
    public void sendVerificationCode(String email) {
        UserEmail userEmail = new UserEmail(email);
        String verificationCode = generateVerificationCode();
        redisUtil.set(RedisKey.verificationCodeKey(userEmail), verificationCode, 300);
        emailService.sendEmailAsync(email, "Verification Code", "Your verification code is: " + verificationCode);
    }

    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 1000000));
    }

    public void logout(User user) {
        redisUtil.delete(RedisKey.userKey(user.getId()));//todo

    }
}
