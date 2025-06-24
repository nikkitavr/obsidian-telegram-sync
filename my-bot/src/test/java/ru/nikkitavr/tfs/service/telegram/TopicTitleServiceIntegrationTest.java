package ru.nikkitavr.tfs.service.telegram;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.nikkitavr.tfs.infra.db.ChatTitlesRepository;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class TopicTitleServiceIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("test-db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Autowired
    private TopicTitleService topicTitleService;
    @Autowired
    private ChatTitlesRepository chatTitlesRepository;

    @BeforeEach
    void setUp() {
        chatTitlesRepository.deleteAll();
    }

    @Test
    void testSetAndGetTitleForTopic() {
        Long chatId = 12345L;
        Integer topicId = 678;
        String title = "Test Topic";

        topicTitleService.setTitleForTopic(chatId, topicId, title);
        Optional<String> found = topicTitleService.getTitleForTopic(chatId, topicId);
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(title);
    }

    @Test
    void testGetTitleForNonexistentTopic() {
        Optional<String> found = topicTitleService.getTitleForTopic(999L, 888);
        assertThat(found).isNotPresent();
    }
} 