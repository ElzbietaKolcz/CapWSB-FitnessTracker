package pl.wsb.fitnesstracker.mail.api;

/**
 * Data transfer object representing an email message.
 * <p>
 * This record contains all the necessary information to send an email:
 * the recipient address, the sender address, the subject, and the content.
 * </p>
 *
 * @param toAddress the recipient's email address
 * @param from      the sender's email address
 * @param subject   the subject of the email
 * @param content   the body/content of the email
 */
public record EmailDto(String toAddress, String from, String subject, String content) {

}
