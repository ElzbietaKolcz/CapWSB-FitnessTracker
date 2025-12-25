package pl.wsb.fitnesstracker.training.api;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.wsb.fitnesstracker.training.internal.ActivityType;
import pl.wsb.fitnesstracker.user.api.User;

import java.util.Date;

/**
 * Represents a training session performed by a user.
 * <p>
 * A training consists of a time interval, an activity type,
 * and optional performance metrics such as distance and average speed.
 * Each training is associated with exactly one {@link User}.
 */
@Entity
@Table(name = "trainings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Training {

    /**
     * Unique identifier of the training.
     * <p>
     * Generated automatically by the persistence provider.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User who performed the training.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Start time of the training session.
     */
    @Column(name = "start_time", nullable = false)
    private Date startTime;

    /**
     * End time of the training session.
     */
    @Column(name = "end_time", nullable = false)
    private Date endTime;

    /**
     * Type of activity performed during the training.
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "activity_type", nullable = false)
    private ActivityType activityType;

    /**
     * Total distance covered during the training.
     * <p>
     * The unit depends on the domain convention (e.g. kilometers or meters).
     */
    @Column(name = "distance")
    private double distance;

    /**
     * Average speed achieved during the training.
     * <p>
     * The unit depends on the domain convention (e.g. km/h).
     */
    @Column(name = "average_speed")
    private double averageSpeed;

    /**
     * Creates a new training instance.
     *
     * @param user         the user who performed the training
     * @param startTime    the start time of the training
     * @param endTime      the end time of the training
     * @param activityType the type of activity performed
     * @param distance     the total distance covered
     * @param averageSpeed the average speed achieved
     */
    public Training(
            final User user,
            final Date startTime,
            final Date endTime,
            final ActivityType activityType,
            final double distance,
            final double averageSpeed) {
        this.user = user;
        this.startTime = startTime;
        this.endTime = endTime;
        this.activityType = activityType;
        this.distance = distance;
        this.averageSpeed = averageSpeed;
    }
}
