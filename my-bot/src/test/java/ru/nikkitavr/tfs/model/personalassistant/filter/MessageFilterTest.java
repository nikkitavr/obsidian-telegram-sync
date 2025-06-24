package ru.nikkitavr.tfs.model.personalassistant.filter;

import org.junit.jupiter.api.Test;
import ru.nikkitavr.tfs.model.personalassistant.Message;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.junit.jupiter.api.Assertions.*;

class MessageFilterTest {
    @Test
    void testAll() {
        MessageFilter filter = new MessageFilter("{{all=any}}");
        assertTrue(filter.match(new Message()));
    }

    @Test
    void testUserEqual() {
        User user = new User();
        user.setId(123L);
        user.setUserName("testuser");
        user.setFirstName("Test");
        user.setLastName("User");
        Message msg = new Message();
        msg.setFrom(user);
        MessageFilter filter = new MessageFilter("{{user=123}}");
        assertTrue(filter.match(msg));
        filter = new MessageFilter("{{user=testuser}}");
        assertTrue(filter.match(msg));
        filter = new MessageFilter("{{user=other}}");
        assertFalse(filter.match(msg));
        filter = new MessageFilter("{{user~Test}}");
        assertTrue(filter.match(msg));
    }

    @Test
    void testChatAndContent() {
        Chat chat = new Chat();
        chat.setId(456L);
        chat.setTitle("My Chat");
        Message msg = new Message();
        msg.setChat(chat);
        msg.setText("hello world");
        MessageFilter filter = new MessageFilter("{{chat=My Chat}} && {{content~hello}}");
        assertTrue(filter.match(msg));
        filter = new MessageFilter("{{chat=My Chat}} && {{content~bye}}");
        assertFalse(filter.match(msg));
    }

    @Test
    void testOrAndNegation() {
        Chat chat = new Chat();
        chat.setId(456L);
        chat.setTitle("My Chat");
        Message msg = new Message();
        msg.setChat(chat);
        msg.setText("hello world");
        MessageFilter filter = new MessageFilter("{{chat=Other}} || {{content~hello}}");
        assertTrue(filter.match(msg));
        filter = new MessageFilter("!{{content~hello}} && {{chat=My Chat}}");
        assertFalse(filter.match(msg));
    }

    @Test
    void testTopic() {
        Message msg = new Message();
        msg.setMessageThreadTitle("My Topic");
        MessageFilter filter = new MessageFilter("{{topic=My Topic}}");
        assertTrue(filter.match(msg));
        filter = new MessageFilter("{{topic~Topic}}");
        assertTrue(filter.match(msg));
        filter = new MessageFilter("{{topic=Other}}");
        assertFalse(filter.match(msg));
    }

    @Test
    void testNullCases() {
        MessageFilter filter = new MessageFilter("{{user=any}}");
        assertFalse(filter.match(null));
    }

    @Test
    void testForwardFrom_TODO() {
        // TODO: реализовать тесты, когда будет реализована логика FORWARD_FROM
        Message msg = new Message();
        MessageFilter filter = new MessageFilter("{{forwardFrom=any}}");
        assertTrue(filter.match(msg)); // сейчас всегда true
    }

    @Test
    void testForwardFromVariants() {
        // 1. Forwarded user
        User fwdUser = new User();
        fwdUser.setId(111L);
        fwdUser.setUserName("fwduser");
        fwdUser.setFirstName("Fwd");
        fwdUser.setLastName("User");
        Message msg1 = new Message();
        msg1.setForwardFrom(fwdUser);
        MessageFilter filter1 = new MessageFilter("{{forwardFrom=fwduser}} ");
        assertTrue(filter1.match(msg1));
        filter1 = new MessageFilter("{{forwardFrom~Fwd}} ");
        assertTrue(filter1.match(msg1));
        filter1 = new MessageFilter("{{forwardFrom=other}} ");
        assertFalse(filter1.match(msg1));

        // 2. Forwarded chat
        Chat fwdChat = new Chat();
        fwdChat.setId(222L);
        fwdChat.setTitle("Fwd Channel");
        fwdChat.setUserName("fwdchannel");
        Message msg2 = new Message();
        msg2.setForwardFromChat(fwdChat);
        MessageFilter filter2 = new MessageFilter("{{forwardFrom=222}} ");
        assertTrue(filter2.match(msg2));
        filter2 = new MessageFilter("{{forwardFrom=Fwd Channel}} ");
        assertTrue(filter2.match(msg2));
        filter2 = new MessageFilter("{{forwardFrom~channel}} ");
        assertTrue(filter2.match(msg2));
        filter2 = new MessageFilter("{{forwardFrom=other}} ");
        assertFalse(filter2.match(msg2));

        // 3. Forwarded sender name
        Message msg3 = new Message();
        msg3.setForwardSenderName("Anon Admin");
        MessageFilter filter3 = new MessageFilter("{{forwardFrom=Anon Admin}} ");
        assertTrue(filter3.match(msg3));
        filter3 = new MessageFilter("{{forwardFrom~Admin}} ");
        assertTrue(filter3.match(msg3));
        filter3 = new MessageFilter("{{forwardFrom=other}} ");
        assertFalse(filter3.match(msg3));

        // 4. Fallback: from
        User from = new User();
        from.setId(333L);
        from.setUserName("fromuser");
        from.setFirstName("From");
        from.setLastName("User");
        Message msg4 = new Message();
        msg4.setFrom(from);
        MessageFilter filter4 = new MessageFilter("{{forwardFrom=fromuser}} ");
        assertTrue(filter4.match(msg4));
        filter4 = new MessageFilter("{{forwardFrom~From}} ");
        assertTrue(filter4.match(msg4));
        filter4 = new MessageFilter("{{forwardFrom=other}} ");
        assertFalse(filter4.match(msg4));
    }
} 