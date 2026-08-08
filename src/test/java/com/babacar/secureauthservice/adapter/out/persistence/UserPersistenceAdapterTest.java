package com.babacar.secureauthservice.adapter.out.persistence;


import com.babacar.secureauthservice.domain.model.Role;
import com.babacar.secureauthservice.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class UserPersistenceAdapterTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserPersistenceAdapter adapter;

    @Test
    void should_save_and_retrieve_user() {
        User user = new User(
                UUID.randomUUID(),
                "test@example.com",
                "hashedpassword",
                Role.USER,
                false,    // mfaEnabled
                null,     // mfaSecret
                false
        );

        adapter.save(user);

        var found = adapter.findByEmail("test@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().email()).isEqualTo("test@example.com");
    }

    @Test
    void should_return_empty_when_email_not_found() {
        var result = adapter.findByEmail("notfound@example.com");
        assertThat(result).isEmpty();
    }

    @Test
    void should_return_true_when_email_exists() {
        User user = new User(
                UUID.randomUUID(), "exists@example.com",
                "hash", Role.USER, false, null, false
        );
        adapter.save(user);

        assertThat(adapter.existsByEmail("exists@example.com")).isTrue();
    }

    @Test
    void should_return_false_when_email_not_exists() {
        assertThat(adapter.existsByEmail("nobody@example.com")).isFalse();
    }

    @Test
    void should_find_user_by_id() {
        UUID id = UUID.randomUUID();
        User user = new User(
                id, "byid@example.com",
                "hash", Role.USER, false, null, false
        );
        adapter.save(user);

        var result = adapter.findById(id);
        assertThat(result).isPresent();
        assertThat(result.get().email()).isEqualTo("byid@example.com");
    }

    @Test
    void should_update_mfa_secret() {
        UUID id = UUID.randomUUID();
        User user = new User(
                id, "mfa@example.com",
                "hash", Role.USER, false, null, false
        );
        adapter.save(user);

        User updated = adapter.updateMfaSecret(id, "MYSECRET");

        assertThat(updated.mfaSecret()).isEqualTo("MYSECRET");
        assertThat(updated.mfaVerified()).isFalse();
    }

    @Test
    void should_enable_mfa() {
        UUID id = UUID.randomUUID();
        User user = new User(
                id, "enablemfa@example.com",
                "hash", Role.USER, false, "SECRET", false
        );
        adapter.save(user);

        User updated = adapter.enableMfa(id);

        assertThat(updated.mfaVerified()).isTrue();
    }
}
