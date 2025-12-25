package pl.wsb.fitnesstracker.training.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import pl.wsb.fitnesstracker.user.api.UserDto;
import pl.wsb.fitnesstracker.training.internal.ActivityType;

import java.util.Date;

/**
 * Data Transfer Object representing a training session.
 * <p>
 * This DTO is used to exchange training data between the API layer
 * and external clients. It contains basic training details,
 * timing information, and performance metrics.
 *
 * @param id           the unique identifier of the training
 * @param user         the user associated with the training
 * @param startTime    the start time of the training session (UTC)
 * @param endTime      the end time of the training session (UTC)
 * @param activityType the type of activity performed during the training
 * @param distance     the total distance covered during the training
 * @param averageSpeed the average speed achieved during the training
 */
public record TrainingDto(
        Long id,
        UserDto user,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS+00:00", timezone = "UTC")
        Date startTime,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS+00:00", timezone = "UTC")
        Date endTime,
        ActivityType activityType,
        double distance,
        double averageSpeed
) { }

