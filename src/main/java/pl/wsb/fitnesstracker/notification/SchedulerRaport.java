
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

@Configuration
@EnableScheduling
public class SchedulerRaport {

    private final TrainingProvider trainingProvider;

    private final UserProvider userProvider;

    public SchedulerRaport(TrainingProvider trainingProvider, UserProvider userProvider) {
        this.trainingProvider = trainingProvider;
        this.userProvider = userProvider;
    }

//
//    @Scheduled(cron = "0 0 0 * * 0")
//    public ThreadPoolTaskScheduler scheduletask(){
//        final ThreadPoolTaskScheduler mainScheduler = new ThreadPoolTaskScheduler();
//        mainScheduler.setPoolSize(10);
//        mainScheduler.setThreadNamePrefix("Raport-generatot");
//        mainScheduler.initialize();
//        return mainScheduler;
//    }

    public void printTraining(){

        Date today = Date.from(Instant.now());
        Date sevenDaysAgo = Date.from(Instant.ofEpochMilli(604800));
//        private finaldate = today - sevenDaysAgo;

        List<User> userList = userProvider.findAllUsers();
        for (User user: userList){
            List<Training> trainingListForUser = trainingProvider.getTraining(user.getId());
            List<Training> filteredList = trainingListForUser.stream().filter(training -> training.getStartTime().after(sevenDaysAgo)).filter(training -> training.getEndTime().before(today));
        }
        List<Training> listTrainings = trainingProvider.getAllTrainings();
    }


@Scheduled(cron = "0 0 0 * * 0")
public  void generateReport() {

        List<Training> trainingList = trainingProvider.getAllTrainings();
        // USER1: XYZ
        // USER2: XYZ'
    System.out.println();
    }
// pobrac wszystkie treningi
 }