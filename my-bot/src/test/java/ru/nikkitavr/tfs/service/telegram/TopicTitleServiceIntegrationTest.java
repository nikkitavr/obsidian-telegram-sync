package ru.nikkitavr.tfs.service.telegram;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.nikkitavr.tfs.infra.db.ChatTitlesRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TopicTitleServiceIntegrationTest {

    @Container
    public static GenericContainer<?> sqlite = new GenericContainer<>(DockerImageName.parse("nouchka/sqlite3:latest"))
            .withExposedPorts(3306)
            .withClasspathResourceMapping("/schema.sql", "/docker-entrypoint-initdb.d/schema.sql", BindMode.READ_ONLY);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        String jdbcUrl = String.format("jdbc:sqlite:%s", "/tmp/test-db.sqlite");
        registry.add("spring.datasource.url", () -> jdbcUrl);
        registry.add("spring.datasource.driver-class-name", () -> "org.sqlite.JDBC");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.SQLiteDialect");
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