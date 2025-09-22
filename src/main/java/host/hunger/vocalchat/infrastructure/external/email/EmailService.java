package host.hunger.vocalchat.infrastructure.external.email;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String content);
    void sendEmailAsync(String to, String subject, String content);
}
