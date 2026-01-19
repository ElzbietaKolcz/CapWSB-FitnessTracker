package pl.wsb.fitnesstracker.notification;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
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
 * It is executed automatically based on the configured schedule.
 */
@Configuration
@EnableScheduling
public class SchedulerRaport {

    /**
     * Number of milliseconds in 7 days, used to calculate the weekly reporting window.
     */
    private static final long SEVEN_DAYS_IN_MILLIS = 7L * 24 * 60 * 60 * 1000;

    /**
     * Provider for training sessions, used to retrieve all trainings from the database.
     */
    private final TrainingProvider trainingProvider;

    /**
     * Provider for users, used to retrieve all users from the database.
     */
    private final UserProvider userProvider;

    /**
     * Constructs a SchedulerRaport with the given training and user providers.
     *
     * @param trainingProvider the training provider
     * @param userProvider     the user provider
     */
    public SchedulerRaport(TrainingProvider trainingProvider, UserProvider userProvider) {
        this.trainingProvider = trainingProvider;
        this.userProvider = userProvider;
    }

    /**
     * Scheduled method that generates the weekly training report.
     * <p>
     * It retrieves all trainings from the database and passes them to {@link #printTraining(List)}.
     * <p>
     * Default cron: every Sunday at midnight.
     * For testing purposes, an alternative cron (every 10 seconds) is provided but commented out.
     *
     */
    @Scheduled(cron = "0 0 0 * * 0")
    public Map<User, List<Training>> generateReport() {
        List<Training> trainingList = trainingProvider.getAllTrainings();
        List<User> userList = userProvider.findAllUsers();

        long nowMillis = System.currentTimeMillis();
        long sevenDaysAgoMillis = nowMillis - SEVEN_DAYS_IN_MILLIS;

        Map<User, List<Training>> report = userList.stream().collect(java.util.stream.Collectors.toMap(u -> u, u -> trainingList.stream().filter(t -> {
            assert t.getUser().getId() != null;
            return t.getUser().getId().equals(u.getId());
        }).filter(t -> t.getStartTime().getTime() >= sevenDaysAgoMillis && t.getEndTime().getTime() <= nowMillis).toList()));

        printTraining(trainingList);

        return report;
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
                assert training.getUser().getId() != null;
                if (training.getUser().getId().equals(user.getId()) && training.getStartTime().after(sevenDaysAgo) && training.getEndTime().before(today)) {

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
