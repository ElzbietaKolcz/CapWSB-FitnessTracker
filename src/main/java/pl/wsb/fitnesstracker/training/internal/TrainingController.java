package pl.wsb.fitnesstracker.training.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.training.api.TrainingDto;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/v1/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingMapper trainingMapper;
    private final TrainingServiceImpl trainingService;

    /**
     * Retrieves all trainings assigned to a specific user.
     * <p>
     * The user is identified by their unique identifier.
     * If the user has no trainings assigned, an empty list is returned.
     *
     * @param userId the unique identifier of the user whose trainings are requested
     * @return a list of {@link TrainingDto} representing trainings of the given user;
     *         the list may be empty if no trainings are found
     */
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TrainingDto> getAllTrainingsForUser(@PathVariable Long userId) {
        return trainingService.getTraining(userId)
                .stream()
                .map(trainingMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all trainings available in the system.
     *
     * @return a list of {@link TrainingDto} representing all trainings;
     *         the list may be empty if no trainings exist
     */
    @GetMapping
    public List<TrainingDto> getAllTrainings() {
        return trainingService.getAllTrainings()
                .stream()
                .map(trainingMapper::toDto)
                .collect(Collectors.toList());
    }
}
