package ru.nikkitavr.tfs.infra.db.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Entity
@Table(name = "group_topics_titles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChatTitles implements Serializable {
    @EmbeddedId
    private PK pk;

    @Column(nullable = false)
    private String title;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class PK implements Serializable {
        @Column(name = "chat_id", nullable = false)
        private Long chatId;
        @Column(name = "topic_id", nullable = false)
        private Integer topicId;
    }

    public ChatTitles(Long chatId, Integer topicId, String title) {
        this.pk = new PK(chatId, topicId);
        this.title = title;
    }
}
