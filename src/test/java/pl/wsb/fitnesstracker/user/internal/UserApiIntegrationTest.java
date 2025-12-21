package pl.wsb.fitnesstracker.user.internal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.wsb.fitnesstracker.IntegrationTest;
import pl.wsb.fitnesstracker.IntegrationTestBase;
import pl.wsb.fitnesstracker.user.api.User;

import java.time.LocalDate;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
class UserApiIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    public static User generateUser() {
        return new User(randomUUID().toString(), randomUUID().toString(), LocalDate.now(), randomUUID().toString());
    }

    /**
     * Creates a valid {@link User} instance with a predefined birth date.
     *
     * @param date the birth date to assign to the user
     * @return a new {@link User} with the given birth date
     */

    private static User generateUserWithDate(LocalDate date) {
        return new User(randomUUID().toString(), randomUUID().toString(), date, randomUUID().toString());
    }

    /**
     * Verifies that all users are returned when requesting the users endpoint.
     * <p>
     * Given multiple users stored in the database,
     * when the {@code GET /v1/users} endpoint is called,
     * then all users should be returned with full details.
     */

    @Test
    void shouldReturnAllUsers_whenGettingAllUsers() throws Exception {
        User user1 = existingUser(generateUser());
        User user2 = existingUser(generateUser());

        mockMvc.perform(get("/v1/users").contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value(user1.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(user1.getLastName()))
                .andExpect(jsonPath("$[0].birthdate").value(ISO_DATE.format(user1.getBirthdate())))

                .andExpect(jsonPath("$[1].firstName").value(user2.getFirstName()))
                .andExpect(jsonPath("$[1].lastName").value(user2.getLastName()))
                .andExpect(jsonPath("$[1].birthdate").value(ISO_DATE.format(user2.getBirthdate())))

                .andExpect(jsonPath("$[2]").doesNotExist());
    }

    /**
     * Verifies that a simplified representation of users is returned.
     * <p>
     * Given multiple users stored in the database,
     * when the {@code GET /v1/users/simple} endpoint is called,
     * then only basic user information should be returned.
     */

    @Test
    void shouldReturnAllSimpleUsers_whenGettingAllUsers() throws Exception {
        User user1 = existingUser(generateUser());
        User user2 = existingUser(generateUser());

        mockMvc.perform(get("/v1/users/simple").contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value(user1.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(user1.getLastName()))

                .andExpect(jsonPath("$[1].firstName").value(user2.getFirstName()))
                .andExpect(jsonPath("$[1].lastName").value(user2.getLastName()))

                .andExpect(jsonPath("$[2]").doesNotExist());
    }

    /**
     * Verifies that detailed user information is returned when querying by ID.
     * <p>
     * Given a user existing in the database,
     * when the {@code GET /v1/users/{id}} endpoint is called,
     * then the full user details should be returned.
     */

    @Test
    void shouldReturnDetailsAboutUser_whenGettingUserById() throws Exception {
        User user1 = existingUser(generateUser());

        mockMvc.perform(get("/v1/users/{id}", user1.getId()).contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value(user1.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user1.getLastName()))
                .andExpect(jsonPath("$.birthdate").value(ISO_DATE.format(user1.getBirthdate())))
                .andExpect(jsonPath("$.email").value(user1.getEmail()));

    }

    /**
     * Verifies that users can be retrieved by email address.
     * <p>
     * Given a user with a specific email,
     * when the {@code GET /v1/users/email} endpoint is called,
     * then the matching user should be returned.
     */

    @Test
    void shouldReturnDetailsAboutUser_whenGettingUserByEmail() throws Exception {
        User user1 = existingUser(generateUser());

        mockMvc.perform(get("/v1/users/email").param("email", user1.getEmail()).contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(user1.getId().intValue()))
                .andExpect(jsonPath("$[0].email").value(user1.getEmail()));
    }

    /**
     * Verifies that only users older than the specified date are returned.
     * <p>
     * Given users with different birth dates,
     * when the {@code GET /v1/users/older/{time}} endpoint is called,
     * then only users older than the provided date should be returned.
     */
    @Test
    void shouldReturnAllUsersOlderThan_whenGettingAllUsersOlderThan() throws Exception {
        User user1 = existingUser(generateUserWithDate(LocalDate.of(2000, 8, 11)));
        existingUser(generateUserWithDate(LocalDate.of(2024, 8, 11)));


        mockMvc.perform(get("/v1/users/older/{time}", LocalDate.of(2024, 8, 10)).contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value(user1.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(user1.getLastName()))
                .andExpect(jsonPath("$[0].birthdate").value(ISO_DATE.format(user1.getBirthdate())))

                .andExpect(jsonPath("$[1]").doesNotExist());
    }

    /**
     * Verifies that a user is removed from the system.
     * <p>
     * Given an existing user,
     * when the {@code DELETE /v1/users/{id}} endpoint is called,
     * then the user should be removed from the repository.
     */

    @Test
    void shouldRemoveUserFromRepository_whenDeletingClient() throws Exception {
        User user1 = existingUser(generateUser());


        mockMvc.perform(delete("/v1/users/{userId}", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isNoContent());

        List<User> allUser = getAllUsers();
        assertThat(allUser).isEmpty();

    }

    /**
     * Verifies that a new user is persisted when creating a user.
     * <p>
     * Given valid user data,
     * when the {@code POST /v1/users} endpoint is called,
     * then the user should be stored in the database.
     */

    @Test
    void shouldPersistUser_whenCreatingUser() throws Exception {

        final String USER_NAME = "Mike";
        final String USER_LAST_NAME = "Scott";
        final String USER_BIRTHDATE = "1999-09-29";
        final String USER_EMAIL = "mike.scott@domain.com";

        String creationRequest = """
                
                {
                "firstName": "%s",
                "lastName": "%s",
                "birthdate": "%s",
                "email": "%s"
                }
                """.formatted(
                USER_NAME,

                USER_LAST_NAME,
                USER_BIRTHDATE,
                USER_EMAIL);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isCreated());

        List<User> allUsers = getAllUsers();
        User user = allUsers.get(0);

        assertThat(user.getFirstName()).isEqualTo(USER_NAME);
        assertThat(user.getLastName()).isEqualTo(USER_LAST_NAME);
        assertThat(user.getBirthdate()).isEqualTo(LocalDate.parse(USER_BIRTHDATE));
        assertThat(user.getEmail()).isEqualTo(USER_EMAIL);

    }

    /**
     * Verifies that an existing user can be updated.
     * <p>
     * Given an existing user,
     * when the {@code PUT /v1/users/{id}} endpoint is called with new data,
     * then the user data should be updated in the database.
     */

    @Test
    void shouldUpdateUser_whenUpdatingUser() throws Exception {
        User user1 = existingUser(generateUser());

        final String USER_NAME = "Mike";
        final String USER_LAST_NAME = "Scott";
        final String USER_BIRTHDATE = "1999-09-29";
        final String USER_EMAIL = "mike.scott@domain.com";

        String updateRequest = """
                
                {
                "firstName": "%s",
                "lastName": "%s",
                "birthdate": "%s",
                "email": "%s"
                }
                """.formatted(
                USER_NAME,

                USER_LAST_NAME,
                USER_BIRTHDATE,
                USER_EMAIL);

        mockMvc.perform(put("/v1/users/{userId}", user1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest));

        List<User> allUsers = getAllUsers();
        User user = allUsers.get(0);

        assertThat(user.getFirstName()).isEqualTo(USER_NAME);
        assertThat(user.getLastName()).isEqualTo(USER_LAST_NAME);
        assertThat(user.getBirthdate()).isEqualTo(LocalDate.parse(USER_BIRTHDATE));
        assertThat(user.getEmail()).isEqualTo(USER_EMAIL);
    }


}
