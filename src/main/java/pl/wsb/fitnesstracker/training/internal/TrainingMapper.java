package pl.wsb.fitnesstracker.training.internal;

import org.springframework.stereotype.Component;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.api.TrainingDto;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.internal.UserMapper;

/**
 * Maps {@link Training} domain entities to {@link TrainingDto} objects
 * and vice versa.
 * <p>
 * This mapper is responsible for translating training-related data
 * between the internal domain model and the external API representation.
 */
@Component
public class TrainingMapper {

    private final UserMapper userMapper;

    /**
     * Creates a new {@link TrainingMapper}.
     *
     * @param userMapper mapper used to convert {@link User} entities
     *                   to their DTO representation
     */
    public TrainingMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * Converts a {@link Training} entity into a {@link TrainingDto}.
     * <p>
     * If the training has an associated user, the user will also be mapped
     * to its DTO representation. Otherwise, the user field will be {@code null}.
     *
     * @param training the training entity to be converted
     * @return a {@link TrainingDto} representing the given training entity
     */
    public TrainingDto toDto(Training training) {
        return new TrainingDto(
                training.getId(),
                training.getUser() != null ? userMapper.toDto(training.getUser()) : null,
                training.getStartTime(),
                training.getEndTime(),
                training.getActivityType(),
                training.getDistance(),
                training.getAverageSpeed()
        );
    }

    /**
     * Converts a {@link TrainingDto} into a {@link Training} entity.
     * <p>
     * The associated {@link User} must be provided explicitly and will be
     * assigned to the created training entity.
     *
     * @param dto  the training DTO containing training data
     * @param user the user to be associated with the training
     * @return a {@link Training} entity created from the given DTO and user
     */
    public Training toEntity(TrainingDto dto, User user) {
        return new Training(
                user,
                dto.startTime(),
                dto.endTime(),
                dto.activityType(),
                dto.distance(),
                dto.averageSpeed()
        );
    }
}
