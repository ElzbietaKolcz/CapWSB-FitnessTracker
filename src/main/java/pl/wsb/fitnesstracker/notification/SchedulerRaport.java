package pl.wsb.fitnesstracker.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import pl.wsb.fitnesstracker.mail.api.EmailDto;
import pl.wsb.fitnesstracker.mail.api.JavaMailEmailSender;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.api.TrainingProvider;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserProvider;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * SchedulerRaport is responsible for generating weekly training reports
 * for all users in the system.
 * <p>
 * The report lists all trainings performed in the last 7 days for each user,
 * including start and end times, activity type, distance, and average speed.
 * Additionally, a summary email is sent to each user with the number of trainings recorded.
 * <p>
 * This class uses Spring's scheduling support to run periodically according to the configured cron expression.
 */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulerRaport {

    /**
     * Number of milliseconds in 7 days, used to calculate the weekly reporting window.
     */
    private static final long SEVEN_DAYS_IN_MILLIS = 7L * 24 * 60 * 60 * 1000;

    /**
     * Default email sender address.
     */
    private static final String EMAIL_FROM = "no-reply@fitnesstracker.com";

    private final JavaMailEmailSender emailSender;

    /**
     * Provider for training sessions, used to retrieve all trainings from the database.
     */
    private final TrainingProvider trainingProvider;

    /**
     * Provider for users, used to retrieve all users from the database.
     */
    private final UserProvider userProvider;

    /**
     * Generates the {@link EmailDto} for a user based on their training data.
     *
     * @param entry entry from the report mapping users to their trainings
     * @param user  the user to generate the email for
     * @return an {@link EmailDto} containing the email content
     */
    private static EmailDto getEmailDto(Map.Entry<User, List<Training>> entry, User user) {
        List<Training> trainings = entry.getValue();
        String content;

        if (trainings.isEmpty()) {
            content = "Hello " + user.getFirstName() + ",\n\n" + "You didn't record any trainings in the last 7 days.\n" + "Let's get moving and start tracking your workouts!\n\n" + "Best regards,\nFitness Tracker Team";
        } else {
            content = "Hello " + user.getFirstName() + ",\n\n" + "You have " + trainings.size() + " trainings recorded in the last 7 days.\n" + "Keep up the good work!\n\n" + "Best regards,\nFitness Tracker Team";
        }

        return new EmailDto(user.getEmail(), EMAIL_FROM, "Your weekly training report", content);
    }

    /**
     * Scheduled method that generates the weekly training report.
     * <p>
     * Retrieves all trainings and users from the database, filters trainings
     * from the last 7 days for each user, prints them to the console, and sends
     * a summary email to each user.
     * <p>
     * Cron expression: "0 0 0 * * 0" (every Sunday at midnight).
     *
     * @return a map of users to their trainings in the last 7 days
     */
    @Scheduled(cron = "0 0 0 * * 0")
    public Map<User, List<Training>> generateReport() {
        List<Training> trainingList = trainingProvider.getAllTrainings();
        List<User> userList = userProvider.findAllUsers();

        long nowMillis = System.currentTimeMillis();
        long sevenDaysAgoMillis = nowMillis - SEVEN_DAYS_IN_MILLIS;

        Map<User, List<Training>> report = userList.stream().collect(java.util.stream.Collectors.toMap(u -> u, u -> trainingList.stream().filter(t -> t.getUser().getId() != null && t.getUser().getId().equals(u.getId())).filter(t -> t.getStartTime().getTime() >= sevenDaysAgoMillis && t.getEndTime().getTime() <= nowMillis).toList()));

        printTraining(trainingList);
        sendEmailReport(report);

        return report;
    }

    /**
     * Sends a summary email to each user with the number of trainings recorded.
     * <p>
     * Emails are throttled with a 1-second pause between each send to avoid
     * exceeding SMTP server limits.
     *
     * @param report map of users to their trainings in the last 7 days
     */
    private void sendEmailReport(Map<User, List<Training>> report) {
        for (Map.Entry<User, List<Training>> entry : report.entrySet()) {
            User user = entry.getKey();
            EmailDto email = getEmailDto(entry, user);

            try {
                emailSender.send(email);
                log.info("Email sent to {}", user.getEmail());
            } catch (Exception e) {
                log.error("Failed to send email to {}", user.getEmail(), e);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Email sending interrupted for user {}", user.getEmail());
            }
        }
    }

    /**
     * Prints detailed information about trainings for each user in the last 7 days.
     * <p>
     * For each user, the report includes:
     * <ul>
     *     <li>Start and end time of each training session</li>
     *     <li>Activity type (RUNNING, CYCLING, WALKING, etc.)</li>
     *     <li>Distance covered (in kilometers)</li>
     *     <li>Average speed (in km/h)</li>
     * </ul>
     * If the user has no trainings in the last week, it prints "No trainings this week".
     *
     * @param trainingList a list of all trainings retrieved from the database
     */
    public void printTraining(List<Training> trainingList) {
        Date today = Date.from(Instant.now());
        Date sevenDaysAgo = Date.from(Instant.ofEpochMilli(Instant.now().toEpochMilli() - SEVEN_DAYS_IN_MILLIS));
        List<User> userList = userProvider.findAllUsers();

        for (User user : userList) {
            System.out.println("User: " + user.getFirstName());
            boolean hasTrainings = false;

            for (Training training : trainingList) {
                if (training.getUser().getId() != null && training.getUser().getId().equals(user.getId()) && training.getStartTime().after(sevenDaysAgo) && training.getEndTime().before(today)) {

                    System.out.println("  - Training from: " + training.getStartTime() + " to: " + training.getEndTime() + ", Type: " + training.getActivityType() + ", Distance: " + training.getDistance() + " km" + ", Average speed: " + training.getAverageSpeed() + " km/h");
                    hasTrainings = true;
                }
            }

            if (!hasTrainings) {
                System.out.println("  No trainings this week");
            }
            System.out.println();
        }
    }
}
