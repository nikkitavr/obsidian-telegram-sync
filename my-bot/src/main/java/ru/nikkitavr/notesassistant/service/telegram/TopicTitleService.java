package ru.nikkitavr.notesassistant.service.telegram;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nikkitavr.notesassistant.infra.db.ChatTitlesRepository;
import ru.nikkitavr.notesassistant.infra.db.entity.ChatTitles;

@Service
public class TopicTitleService {

    private final ChatTitlesRepository chatTitlesRepository;

    @Autowired
    public TopicTitleService(ChatTitlesRepository chatTitlesRepository) {
        this.chatTitlesRepository = chatTitlesRepository;
    }

    @Transactional
    public void setTitleForTopic(Long chatId, Integer topicId, String title) {
        ChatTitles entity = new ChatTitles(chatId, topicId, title);
        chatTitlesRepository.save(entity);
    }

    public Optional<String> getTitleForTopic(Long chatId, Integer topicId) {
        return chatTitlesRepository.findTitleByChatIdAndTopicId(chatId, topicId);
    }
}
