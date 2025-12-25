package pl.wsb.fitnesstracker.user.api;

import jakarta.annotation.Nullable;

/**
 * Simple Data Transfer Object (DTO) representing a user with minimal information.
 * <p>
 * This record is used to transfer basic user details, typically in scenarios
 * where only the user's ID and name are needed.
 * </p>
 *
 * @param id        the unique identifier of the user; can be {@code null} for new users
 * @param firstName the first name of the user; must not be {@code null}
 * @param lastName  the last name of the user; must not be {@code null}
 */
public record UserSimpleDto(@Nullable Long id,
                            String firstName,
                            String lastName) {
}