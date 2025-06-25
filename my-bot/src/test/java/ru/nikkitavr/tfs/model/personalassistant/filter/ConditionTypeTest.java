package ru.nikkitavr.tfs.model.personalassistant.filter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import ru.nikkitavr.tfs.model.personalassistant.message.Message;
import ru.nikkitavr.tfs.model.personalassistant.message.TelegramChat;
import ru.nikkitavr.tfs.model.personalassistant.message.TelegramUser;
import ru.nikkitavr.tfs.model.personalassistant.message.files.Voice;

class ConditionTypeTest {
    @Test
    void testAllAlwaysTrue() {
        assertTrue(ConditionType.ALL.match(new Message(), ConditionOperation.EQUAL, "any"));
    }

    @Test
    void testUserEqualAndContain() {
        TelegramUser user = new TelegramUser();
        user.setId(123L);
        user.setUserName("testuser");
        user.setFirstName("Test");
        user.setLastName("User");
        Message msg = new Message();
        msg.setFrom(user);
        assertTrue(ConditionType.USER.match(msg, ConditionOperation.EQUAL, "testuser"));
        assertTrue(ConditionType.USER.match(msg, ConditionOperation.EQUAL, "123"));
        assertTrue(ConditionType.USER.match(msg, ConditionOperation.EQUAL, "Test User"));
        assertFalse(ConditionType.USER.match(msg, ConditionOperation.EQUAL, "other"));
        assertTrue(ConditionType.USER.match(msg, ConditionOperation.CONTAIN, "test"));
        assertTrue(ConditionType.USER.match(msg, ConditionOperation.CONTAIN, "User"));
        assertFalse(ConditionType.USER.match(msg, ConditionOperation.CONTAIN, "other"));
    }

    @Test
    void testChat() {
        TelegramChat chat = new TelegramChat();
        chat.setId(456L);
        chat.setTitle("My Chat");
        chat.setUserName("chatuser");
        Message msg = new Message();
        msg.setChat(chat);
        assertTrue(ConditionType.CHAT.match(msg, ConditionOperation.EQUAL, "456"));
        assertTrue(ConditionType.CHAT.match(msg, ConditionOperation.EQUAL, "My Chat"));
        assertTrue(ConditionType.CHAT.match(msg, ConditionOperation.EQUAL, "chatuser"));
        assertFalse(ConditionType.CHAT.match(msg, ConditionOperation.EQUAL, "other"));
        assertTrue(ConditionType.CHAT.match(msg, ConditionOperation.CONTAIN, "Chat"));
        assertFalse(ConditionType.CHAT.match(msg, ConditionOperation.CONTAIN, "other"));
    }

    @Test
    void testTopic() {
        Message msg = new Message();
        msg.setMessageThreadTitle("My Topic");
        assertTrue(ConditionType.TOPIC.match(msg, ConditionOperation.EQUAL, "My Topic"));
        assertFalse(ConditionType.TOPIC.match(msg, ConditionOperation.EQUAL, "Other"));
        assertTrue(ConditionType.TOPIC.match(msg, ConditionOperation.CONTAIN, "Topic"));
        assertFalse(ConditionType.TOPIC.match(msg, ConditionOperation.CONTAIN, "Other"));
    }

    @Test
    void testContent() {
        Message msg = new Message();
        msg.setText("hello world");
        msg.setCaption("caption");
        assertTrue(ConditionType.CONTENT.match(msg, ConditionOperation.EQUAL, "hello world"));
        assertTrue(ConditionType.CONTENT.match(msg, ConditionOperation.CONTAIN, "world"));
        assertTrue(ConditionType.CONTENT.match(msg, ConditionOperation.CONTAIN, "caption"));
        assertFalse(ConditionType.CONTENT.match(msg, ConditionOperation.EQUAL, "other"));
    }

    @Test
    void testVoiceTranscript() {
        Voice voice = new Voice();
        voice.setTranscription("voice text");
        Message msg = new Message();
        msg.setVoice(voice);
        assertTrue(ConditionType.VOICE_TRANSCRIPT.match(msg, ConditionOperation.EQUAL, "voice text"));
        assertTrue(ConditionType.VOICE_TRANSCRIPT.match(msg, ConditionOperation.CONTAIN, "voice"));
        assertFalse(ConditionType.VOICE_TRANSCRIPT.match(msg, ConditionOperation.EQUAL, "other"));
    }

    @Test
    void testNullCases() {
        assertFalse(ConditionType.USER.match(null, ConditionOperation.EQUAL, "any"));
        assertFalse(ConditionType.CHAT.match(null, ConditionOperation.EQUAL, "any"));
        assertFalse(ConditionType.TOPIC.match(null, ConditionOperation.EQUAL, "any"));
        assertFalse(ConditionType.CONTENT.match(null, ConditionOperation.EQUAL, "any"));
        assertFalse(ConditionType.VOICE_TRANSCRIPT.match(null, ConditionOperation.EQUAL, "any"));
    }

    @Test
    void testUserFullNameVariants() {
        TelegramUser user = new TelegramUser();
        user.setId(123L);
        user.setUserName("testuser");
        user.setFirstName("Test");
        user.setLastName("User");
        Message msg = new Message();
        msg.setFrom(user);
        // Проверяем все варианты
        assertTrue(ConditionType.USER.match(msg, ConditionOperation.EQUAL, "testuser"));
        assertTrue(ConditionType.USER.match(msg, ConditionOperation.EQUAL, "123"));
        assertTrue(ConditionType.USER.match(msg, ConditionOperation.EQUAL, "Test"));
        assertTrue(ConditionType.USER.match(msg, ConditionOperation.EQUAL, "User"));
        assertTrue(ConditionType.USER.match(msg, ConditionOperation.EQUAL, "Test User"));
        assertFalse(ConditionType.USER.match(msg, ConditionOperation.EQUAL, "other"));
        assertTrue(ConditionType.USER.match(msg, ConditionOperation.CONTAIN, "test"));
        assertTrue(ConditionType.USER.match(msg, ConditionOperation.CONTAIN, "User"));
        assertFalse(ConditionType.USER.match(msg, ConditionOperation.CONTAIN, "other"));
    }

    @Test
    void testForwardFromFullVariants() {
        // 1. Forwarded user
        TelegramUser fwdUser = new TelegramUser();
        fwdUser.setId(111L);
        fwdUser.setUserName("fwduser");
        fwdUser.setFirstName("Fwd");
        fwdUser.setLastName("User");
        Message msg1 = new Message();
        msg1.setForwardFrom(fwdUser);
        assertTrue(ConditionType.FORWARD_FROM.match(msg1, ConditionOperation.EQUAL, "fwduser"));
        assertTrue(ConditionType.FORWARD_FROM.match(msg1, ConditionOperation.EQUAL, "111"));
        assertTrue(ConditionType.FORWARD_FROM.match(msg1, ConditionOperation.EQUAL, "Fwd"));
        assertTrue(ConditionType.FORWARD_FROM.match(msg1, ConditionOperation.EQUAL, "User"));
        assertTrue(ConditionType.FORWARD_FROM.match(msg1, ConditionOperation.EQUAL, "Fwd User"));
        assertFalse(ConditionType.FORWARD_FROM.match(msg1, ConditionOperation.EQUAL, "other"));
        assertTrue(ConditionType.FORWARD_FROM.match(msg1, ConditionOperation.CONTAIN, "fwd"));
        assertTrue(ConditionType.FORWARD_FROM.match(msg1, ConditionOperation.CONTAIN, "User"));
        assertFalse(ConditionType.FORWARD_FROM.match(msg1, ConditionOperation.CONTAIN, "other"));

        // 2. Forwarded chat + signature
        TelegramChat fwdChat = new TelegramChat();
        fwdChat.setId(222L);
        fwdChat.setTitle("Fwd Channel");
        fwdChat.setUserName("fwdchannel");
        Message msg2 = new Message();
        msg2.setForwardFromChat(fwdChat);
        msg2.setForwardSignature("sig");
        assertTrue(ConditionType.FORWARD_FROM.match(msg2, ConditionOperation.EQUAL, "222"));
        assertTrue(ConditionType.FORWARD_FROM.match(msg2, ConditionOperation.EQUAL, "Fwd Channel"));
        assertTrue(ConditionType.FORWARD_FROM.match(msg2, ConditionOperation.EQUAL, "fwdchannel"));
        assertTrue(ConditionType.FORWARD_FROM.match(msg2, ConditionOperation.EQUAL, "Fwd Channel (sig)"));
        assertTrue(ConditionType.FORWARD_FROM.match(msg2, ConditionOperation.CONTAIN, "sig"));
        assertFalse(ConditionType.FORWARD_FROM.match(msg2, ConditionOperation.EQUAL, "other"));

        // 3. Forwarded sender name
        Message msg3 = new Message();
        msg3.setForwardSenderName("Anon Admin");
        assertTrue(ConditionType.FORWARD_FROM.match(msg3, ConditionOperation.EQUAL, "Anon Admin"));
        assertTrue(ConditionType.FORWARD_FROM.match(msg3, ConditionOperation.CONTAIN, "Admin"));
        assertFalse(ConditionType.FORWARD_FROM.match(msg3, ConditionOperation.EQUAL, "other"));

        // 4. Fallback: from
        TelegramUser from = new TelegramUser();
        from.setId(333L);
        from.setUserName("fromuser");
        from.setFirstName("From");
        from.setLastName("User");
        Message msg4 = new Message();
        msg4.setFrom(from);
        assertTrue(ConditionType.FORWARD_FROM.match(msg4, ConditionOperation.EQUAL, "fromuser"));
        assertTrue(ConditionType.FORWARD_FROM.match(msg4, ConditionOperation.EQUAL, "333"));
        assertTrue(ConditionType.FORWARD_FROM.match(msg4, ConditionOperation.EQUAL, "From"));
        assertTrue(ConditionType.FORWARD_FROM.match(msg4, ConditionOperation.EQUAL, "User"));
        assertTrue(ConditionType.FORWARD_FROM.match(msg4, ConditionOperation.EQUAL, "From User"));
        assertFalse(ConditionType.FORWARD_FROM.match(msg4, ConditionOperation.EQUAL, "other"));
        assertTrue(ConditionType.FORWARD_FROM.match(msg4, ConditionOperation.CONTAIN, "from"));
        assertTrue(ConditionType.FORWARD_FROM.match(msg4, ConditionOperation.CONTAIN, "User"));
        assertFalse(ConditionType.FORWARD_FROM.match(msg4, ConditionOperation.CONTAIN, "other"));
    }
} 