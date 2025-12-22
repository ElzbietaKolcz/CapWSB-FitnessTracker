package pl.wsb.fitnesstracker.user.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserDto;
import pl.wsb.fitnesstracker.user.api.UserProvider;
import pl.wsb.fitnesstracker.user.api.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
class UserServiceImpl implements UserService, UserProvider {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    /**
     * Creates and persists a new user in the system.
     * <p>
     * The user must not have an assigned identifier. If an ID is already present,
     * the creation will be rejected.
     *
     * @param user the user dto to be created
     * @return the persisted {@link User} with a generated identifier
     * @throws IllegalArgumentException if the user already has an ID assigned
     */
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public User createUser(final UserDto user) {
        log.info("Creating User {}", user);

        if (user.id() != null) {
            Optional<User> optionalUser = userRepository.findById(user.id());
            if (!optionalUser.isPresent()) {
                throw new IllegalArgumentException( "User has already DB ID, update is not permitted!");
            }
        }

        User userEntity = userMapper.toEntity(user);
        return userRepository.save(userEntity);
    }


    /**
     * Retrieves a user by its unique identifier.
     *
     * @param userId the unique identifier of the user
     * @return an {@link Optional} containing the {@link User} if found,
     *         or {@link Optional#empty()} if no user exists with the given ID
     */
    @Override
    public Optional<User> getUser(final Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Retrieves a user by email address.
     *
     * @param email the email address of the user
     * @return an {@link Optional} containing the {@link User} if found,
     *         or {@link Optional#empty()} if no user exists with the given email
     */
    @Override
    public Optional<User> getUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Retrieves all users stored in the system.
     *
     * @return a list of all {@link User} entities;
     *         the list may be empty if no users exist
     */
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves users whose birth date is earlier than the given date.
     *
     * @param date the reference date used to filter users by age
     * @return a list of {@link User} entities older than the given date;
     *         the list may be empty if no matching users are found
     */
    public List<User> getUsersOlderThan(LocalDate date) {
        return userRepository.findAllByBirthdateBefore(date);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }


    public User updateUser(UserDto user) {
        log.info("Update User {}", user);

        if (user.id() != null) {
            Optional<User> optionalUser = userRepository.findById(user.id());
            if (!optionalUser.isPresent()) {
                throw new IllegalArgumentException("User not exist!");
            }

            User existingUser = optionalUser.get();

            existingUser.setFirstName(user.firstName());
            existingUser.setLastName(user.lastName());
            existingUser.setEmail(user.email());
            existingUser.setBirthdate(user.birthdate());

            return userRepository.save(existingUser);
        }

        User newUser = userMapper.toEntity(user);
        return userRepository.save(newUser);
    }




}