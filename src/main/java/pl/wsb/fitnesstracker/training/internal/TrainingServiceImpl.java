package pl.wsb.fitnesstracker.training.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.api.TrainingProvider;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingServiceImpl implements TrainingProvider {

    private final TrainingRepository trainingRepository;

    /**
     * Retrieves all trainings stored in the system.
     *
     * @return a list of all {@link Training} entities;
     *         the list may be empty if no trainings exist
     */
    @Override
    public List<Training> getAllTrainings() {
        return trainingRepository.findAll();
    }

    /**
     * Retrieves all trainings assigned to a specific user.
     * <p>
     * Trainings are filtered based on the user's unique identifier.
     * If the user has no trainings assigned, an empty list is returned.
     *
     * @param userId the unique identifier of the user whose trainings are requested
     * @return a list of {@link Training} entities belonging to the given user;
     *         the list may be empty if no trainings are found
     */
    @Override
    public List<Training> getTraining(Long userId) {
        return trainingRepository.findByUserId(userId);
    }
}
