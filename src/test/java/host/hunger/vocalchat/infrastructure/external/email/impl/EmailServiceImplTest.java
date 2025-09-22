package host.hunger.vocalchat.infrastructure.external.email.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    private final String fromEmail = "3287426755@qq.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", fromEmail);
    }

    @Test
    void sendSimpleEmail_ShouldSendEmailWithCorrectParameters() {
        // 准备测试数据
        String to = "3287426755@qq.com";
        String subject = "Test Subject";
        String content = "Test email content";

        // 执行测试
        emailService.sendSimpleEmail(to, subject, content);

        // 验证邮件发送器被调用，并捕获参数
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();

        // 验证邮件内容
        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals(fromEmail, sentMessage.getFrom());
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(content, sentMessage.getText());
    }

    @Test
    void sendSimpleEmail_WithMultipleRecipients_ShouldSendToAll() {
        // 准备测试数据 - 多个收件人
        String[] to = {"recipient1@example.com", "recipient2@example.com"};
        String subject = "Test Subject";
        String content = "Test email content";

        // 执行测试
        emailService.sendSimpleEmail(to[0] + "," + to[1], subject, content);

        // 验证
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();

        // 验证收件人数量
        assertEquals(2, sentMessage.getTo().length);
        assertEquals(to[0], sentMessage.getTo()[0]);
        assertEquals(to[1], sentMessage.getTo()[1]);
    }

    @Test
    void sendEmailAsync_ShouldCallSendSimpleEmail() throws InterruptedException {
        // 准备测试数据
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String content = "Test email content";

        // 执行测试
        emailService.sendEmailAsync(to, subject, content);

        // 给异步线程一点时间执行
        Thread.sleep(100);

        // 验证邮件发送器被调用
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendSimpleEmail_WhenMailSenderThrowsException_ShouldNotPropagate() {
        // 模拟邮件发送器抛出异常
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));

        // 执行测试并验证不会抛出异常
        assertDoesNotThrow(() ->
                emailService.sendSimpleEmail("test@example.com", "Test", "Content")
        );
    }
}