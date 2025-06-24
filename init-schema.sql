-- Инициализация схемы для Telegram FS Sync

-- Создание таблицы для хранения заголовков чатов и топиков
CREATE TABLE IF NOT EXISTS group_topics_titles (
    chat_id BIGINT NOT NULL,
    topic_id INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    PRIMARY KEY (chat_id, topic_id)
);

-- Создание составного индекса для быстрого поиска по chat_id и topic_id
CREATE INDEX IF NOT EXISTS idx_group_topics_titles_chat_topic ON group_topics_titles(chat_id, topic_id);
