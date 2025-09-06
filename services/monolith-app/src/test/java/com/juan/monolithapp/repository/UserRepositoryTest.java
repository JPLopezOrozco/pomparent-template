package com.juan.monolithapp.repository;

import com.juan.monolithapp.model.Role;
import com.juan.monolithapp.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    public void registerUserTest() {
        User user = User.builder()
                .email("test@test.com")
                .name("Test")
                .password("test")
                .role(Role.USER)
                .build();

        userRepository.saveAndFlush(user);

        Assertions.assertThat(user.getEmail()).isEqualTo("test@test.com");
        Assertions.assertThat(user.getName()).isEqualTo("Test");
        Assertions.assertThat(user.getPassword()).isEqualTo("test");
        Assertions.assertThat(user.getRole()).isEqualTo(Role.USER);

    }

    @Test
    public void findUserByEmailTest() {
        User user = User.builder()
                .email("test@test.com")
                .name("Test")
                .password("test")
                .role(Role.USER)
                .build();

        userRepository.save(user);

        Assertions.assertThat(userRepository.findByEmail("test@test.com"));
    }



}
