package pl.wsb.fitnesstracker.user.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserDto;
import pl.wsb.fitnesstracker.user.api.UserSimpleDto;

import java.time.LocalDate;
import java.util.List;

/**
 * UserController is responsible for handling HTTP requests related to user operations.
 * It provides endpoints for retrieving and creating users.
 */
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserServiceImpl userService;
    private final UserMapper userMapper;

    /**
     * Retrieves all users available in the system.
     *
     * @return a list of {@link UserDto} representing all users
     */
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a simplified representation of all users.
     * <p>
     * This endpoint returns a reduced set of user data intended for
     * lightweight use cases (e.g., dropdowns, lists).
     * </p>
     *
     * @return a list of {@link UserSimpleDto} containing basic user information
     */
    @GetMapping("/simple")
    public List<UserSimpleDto> getAllSimpleUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toSimpleDto)
                .toList();
    }

    /**
     * Retrieves a user by its unique identifier.
     *
     * @param id the unique identifier of the user
     * @return a {@link UserDto} representing the requested user
     * @throws NoSuchElementException if no user is found with the given ID
     */
    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUser(id)
                .map(userMapper::toDto)
                .orElseThrow();
    }

    /**
     * Retrieves users with the specified email address.
     *
     * @param email the email address used to filter users
     * @return a list of {@link UserDto} matching the given email;
     *         the list may be empty if no users are found
     */
    @GetMapping("/email")
    public List<UserDto> getUserByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Retrieves users older than the specified date.
     *
     * @param time the reference date; users older than this date will be returned
     * @return a list of {@link UserDto} representing users older than the given date
     */
    @GetMapping("/older/{time}")
    public List<UserDto> getUsersOlderThan(@PathVariable LocalDate time) {
        return userService.getUsersOlderThan(time)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Deletes a user by its unique identifier.
     *
     * @param userId the ID of the user to delete
     * @responseStatus 204 (NO_CONTENT) if the deletion was successful
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    /**
     * Creates a new user with the provided details.
     *
     * @param userDto the user data to create
     * @return a {@link UserDto} representing the newly created user
     * @responseStatus 201 (CREATED) if the user was successfully created
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserDto userDto) {
        User savedUser = userService.createUser(userDto);
        return userMapper.toDto(savedUser);
    }

    /**
     * Updates an existing user with the provided details.
     *
     * @param userId  the ID of the user to update
     * @param userDto the new user data
     * @return a {@link UserDto} representing the updated user
     * @responseStatus 200 (OK) if the update was successful
     */
    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        UserDto dtoWithId = new UserDto(
                userId,
                userDto.firstName(),
                userDto.lastName(),
                userDto.birthdate(),
                userDto.email()
        );

        User savedUser = userService.updateUser(dtoWithId);
        return userMapper.toDto(savedUser);
    }
}