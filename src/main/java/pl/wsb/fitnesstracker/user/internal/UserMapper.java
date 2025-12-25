package pl.wsb.fitnesstracker.user.internal;

import org.springframework.stereotype.Component;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserDto;
import pl.wsb.fitnesstracker.user.api.UserSimpleDto;


/**
 * Component responsible for mapping between {@link User} entities and their corresponding DTOs:
 * {@link UserDto} and {@link UserSimpleDto}.
 * <p>
 * Provides methods for converting from entity to DTO and vice versa.
 * </p>
 */
@Component
public class UserMapper {

    /**
     * Maps a {@link User} entity to a {@link UserDto}.
     *
     * @param user the {@link User} entity to map
     * @return a {@link UserDto} containing all details of the user
     */
    public UserDto toDto(User user) {
        return new UserDto(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthdate(),
                user.getEmail());
    }

    /**
     * Maps a {@link User} entity to a simplified {@link UserSimpleDto}.
     * <p>
     * This method is intended for use cases where only basic user information
     * (ID, first name, last name) is required.
     * </p>
     *
     * @param user the {@link User} entity to map
     * @return a {@link UserSimpleDto} containing minimal user information
     */
    UserSimpleDto toSimpleDto(User user) {
        return new UserSimpleDto(user.getId(),
                user.getFirstName(),
                user.getLastName());
    }

    /**
     * Maps a {@link UserDto} to a {@link User} entity.
     * <p>
     * Useful when creating or updating users from incoming DTOs.
     * </p>
     *
     * @param userDto the {@link UserDto} to map
     * @return a {@link User} entity with fields copied from the DTO
     */
    User toEntity(UserDto userDto) {
        User user  = new User();
        user.setBirthdate(userDto.birthdate());
        user.setEmail(userDto.email());
        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        return user;
    }
}