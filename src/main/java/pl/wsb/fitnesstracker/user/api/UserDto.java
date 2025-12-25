package pl.wsb.fitnesstracker.user.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing a user.
 * <p>
 * This record is used to transfer user information between different layers of the application.
 * It includes basic user details such as ID, first name, last name, birthdate, and email.
 * </p>
 *
 * @param id        the unique identifier of the user; can be {@code null} for new users
 * @param firstName the first name of the user; must not be {@code null}
 * @param lastName  the last name of the user; must not be {@code null}
 * @param birthdate the user's date of birth; formatted as "yyyy-MM-dd"
 * @param email     the user's email address; must not be {@code null}
 */
public record UserDto(@Nullable Long id,
                      String firstName,
                      String lastName,
                      @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthdate,
                      String email) {
}