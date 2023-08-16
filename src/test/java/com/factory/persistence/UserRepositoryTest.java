package com.factory.persistence;

import com.factory.common.FactoryIntegrationTest;
import com.factory.persistence.entity.Role;
import com.factory.persistence.entity.User;
import com.factory.persistence.repository.RoleRepository;
import com.factory.persistence.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FactoryIntegrationTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldFindByUsername() {
        // Given
        final String testUser = "testuser";
        final String password = "password";
        final String mail = "test@example.com";

        User user = new User();
        user.setUsername(testUser);
        user.setPassword(password);
        user.setEmail(mail);
        user.setEnabled(true);
        var result = userRepository.save(user);

        // When
        Optional<User> savedUser = userRepository.findById(result.getId());

        // Then
        assertTrue(savedUser.isPresent());
        assertEquals(testUser, savedUser.get().getUsername());
        assertEquals(password, savedUser.get().getPassword());
        assertEquals(mail, savedUser.get().getEmail());
        assertTrue(savedUser.get().isEnabled());
    }


    @Test
    void shouldValidEmail() {
        // Given
        final String testUser = "testuser";
        final String password = "password";
        final String mail = "test@example.com";

        User user = new User();
        user.setUsername(testUser);
        user.setPassword(password);
        user.setEmail(mail);
        user.setEnabled(true);

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldValidateInvalidEmail() {
        // Given
        final String testUser = "testuser";
        final String password = "password";
        final String invalidMail = "invalid-email";

        User user = new User();
        user.setUsername(testUser);
        user.setPassword(password);
        user.setEmail(invalidMail); // Invalid email format
        user.setEnabled(true);

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("email", violation.getPropertyPath().toString());
        assertEquals("must be a well-formed email address", violation.getMessage());
    }

    @Test
    void testAddUserRoleRelationship() {
        // Given
        final String testUser = "testuser";
        final String password = "password";
        final String mail = "test@example.com";
        final String roleAdmin = "ROLE_ADMIN";

        User user = new User();
        user.setUsername(testUser);
        user.setPassword(password);
        user.setEmail(mail);
        user.setEnabled(true);
        Role role = new Role();
        role.setName(roleAdmin);

        userRepository.save(user);
        roleRepository.save(role);

        // When
        user.getRoles().add(role);
        Optional<User> savedUser = userRepository.findByUsername(testUser);

        // Then
        assertTrue(savedUser.isPresent());
        assertEquals(1, savedUser.get().getRoles().size());
    }

    @Test
    void testRemoveUserRoleRelationship() {
        // Given
        final String testUser = "testuser";
        final String password = "password";
        final String mail = "test@example.com";
        final String roleAdmin = "ROLE_ADMIN";

        User user = new User();
        user.setUsername(testUser);
        user.setPassword(password);
        user.setEmail(mail);
        user.setEnabled(true);
        Role role = new Role();
        role.setName(roleAdmin);

        roleRepository.save(role);
        userRepository.save(user);

        user.getRoles().add(role);
        role.getUsers().add(user);

        // When
        user.removeRole(role);
        userRepository.save(user);

        Optional<User> savedUser = userRepository.findByUsername(testUser);

        // Then
        assertTrue(savedUser.isPresent());
        assertEquals(0, savedUser.get().getRoles().size());
    }

}
