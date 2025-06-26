package ru.nikkitavr.notesassistant.infra.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
