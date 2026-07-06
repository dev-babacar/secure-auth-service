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
                false
        );

        adapter.save(user);

        var found = adapter.findByEmail("test@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().email()).isEqualTo("test@example.com");
    }
}
