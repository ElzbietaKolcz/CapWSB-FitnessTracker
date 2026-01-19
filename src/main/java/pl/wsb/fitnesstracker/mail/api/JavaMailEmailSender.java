package pl.wsb.fitnesstracker.mail.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link EmailSender} that sends emails using
 * Spring's {@link JavaMailSender}.
 * <p>
 * This component constructs a {@link SimpleMailMessage} from the provided
 * {@link EmailDto} and delegates sending to the {@link JavaMailSender}.
 * </p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JavaMailEmailSender implements EmailSender {

    /**
     * Spring's {@link JavaMailSender} used to send email messages.
     */
    private final JavaMailSender javaMailSender;

    /**
     * Sends an email message using {@link JavaMailSender}.
     * <p>
     * This method converts the provided {@link EmailDto} into a {@link SimpleMailMessage},
     * sets the sender, recipient, subject, and content, and then sends the message.
     * </p>
     *
     * @param email the {@link EmailDto} containing the email information
     */
    @Override
    public void send(EmailDto email) {
        log.debug("Sending email to {}", email.toAddress());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email.from());
        message.setTo(email.toAddress());
        message.setSubject(email.subject());
        message.setText(email.content());

        javaMailSender.send(message);
    }
}
