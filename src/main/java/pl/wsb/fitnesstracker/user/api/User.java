package pl.wsb.fitnesstracker.user.api;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.ToString;

import java.time.LocalDate;

/**
 * Represents a user of the fitness tracker system.
 * <p>
 * A user is identified by a unique email address and contains
 * basic personal information required for training management.
 */
@Entity
@Table(name = "users")
@ToString
public class User {

    /**
     * Unique identifier of the user.
     * <p>
     * Generated automatically by the persistence provider.
     * May be {@code null} for transient (not yet persisted) entities.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Nullable
    private Long id;

    /**
     * First name of the user.
     */
    @Column
    private String firstName;

    /**
     * Last name of the user.
     */
    @Column
    private String lastName;

    /**
     * Date of birth of the user.
     */
    @Column(name = "birthdate", nullable = false)
    private LocalDate birthdate;

    /**
     * Email address of the user.
     * <p>
     * Must be unique and not {@code null}.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Creates a new user instance.
     *
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param birthdate the user's date of birth
     * @param email     the user's email address
     */
    public User(
            final String firstName,
            final String lastName,
            final LocalDate birthdate,
            final String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.email = email;
    }

    /**
     * Default constructor required by JPA.
     */
    public User() {
    }

    /**
     * Returns the unique identifier of the user.
     *
     * @return the user ID or {@code null} if the entity has not been persisted yet
     */
    @Nullable
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the user.
     * <p>
     * Intended for framework usage (e.g. JPA).
     *
     * @param id the user ID to set
     */
    public void setId(@Nullable Long id) {
        this.id = id;
    }

    /**
     * Returns the user's first name.
     *
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name.
     *
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the user's last name.
     *
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name.
     *
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the user's date of birth.
     *
     * @return the date of birth of the user
     */
    public LocalDate getBirthdate() {
        return birthdate;
    }

    /**
     * Sets the user's date of birth.
     *
     * @param birthdate the date of birth to set
     */
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * Returns the user's email address.
     *
     * @return the email address of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
