package ru.nikkitavr.tfs.service.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nikkitavr.tfs.infra.db.ChatTitlesRepository;
import ru.nikkitavr.tfs.infra.db.entity.ChatTitles;

import java.util.Optional;

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
