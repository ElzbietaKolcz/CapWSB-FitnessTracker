package pl.wsb.fitnesstracker.notification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.wsb.fitnesstracker.IntegrationTest;
import pl.wsb.fitnesstracker.IntegrationTestBase;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.internal.ActivityType;
import pl.wsb.fitnesstracker.user.api.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
class SchedulerRaportIntegrationTest extends IntegrationTestBase {

    @Autowired
    private SchedulerRaport schedulerRaport;

    private static User generateClient() {
        return new User(
                java.util.UUID.randomUUID().toString(),
                java.util.UUID.randomUUID().toString(),
                now(),
                java.util.UUID.randomUUID().toString()
        );
    }

    private static Training generateTraining(User user) {
        Date end = new Date();
        Date start = new Date(System.currentTimeMillis() - 90 * 60 * 1000);

        return new Training(
                user,
                start,
                end,
                ActivityType.RUNNING,
                10.5,
                8.2
        );
    }

    private static Training generateTrainingWithDetails(
            User user
    ) throws Exception {

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return new Training(
                user,
                sdf.parse("2024-01-01 08:00:00"),
                sdf.parse("2024-01-01 09:00:00"),
                ActivityType.WALKING,
                5.0,
                5.0
        );
    }

    @Test
    void shouldGenerateWeeklyReportForUsers() throws Exception {
        User user1 = existingUser(generateClient());
        User user2 = existingUser(generateClient());
        User user3 = existingUser(generateClient());

        persistTraining(generateTraining(user1));
        persistTraining(generateTraining(user2));
        persistTraining(generateTrainingWithDetails(user3));

        Map<User, List<Training>> report = schedulerRaport.generateReport();

        assertEquals(ActivityType.RUNNING, report.get(user1).get(0).getActivityType());
        assertEquals(10.5, report.get(user1).get(0).getDistance());
        assertEquals(8.2, report.get(user1).get(0).getAverageSpeed());

        assertEquals(1, report.get(user2).size());
        assertEquals(ActivityType.RUNNING, report.get(user2).get(0).getActivityType());
        assertEquals(10.5, report.get(user2).get(0).getDistance());
        assertEquals(8.2, report.get(user2).get(0).getAverageSpeed());

        assertEquals(0, report.get(user3).size());
    }
}
