package host.hunger.vocalchat.application.port;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String content);
    void sendEmailAsync(String to, String subject, String content);
}
