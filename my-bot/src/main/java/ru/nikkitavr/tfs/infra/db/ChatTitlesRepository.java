package ru.nikkitavr.tfs.infra.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.nikkitavr.tfs.infra.db.entity.ChatTitles;
import java.util.Optional;

public interface ChatTitlesRepository extends JpaRepository<ChatTitles, ChatTitles.PK> {
    @Query("SELECT c.title FROM ChatTitles c WHERE c.pk.chatId = :chatId AND c.pk.topicId = :topicId")
    Optional<String> findTitleByChatIdAndTopicId(@Param("chatId") Long chatId, @Param("topicId") Integer topicId);
} 