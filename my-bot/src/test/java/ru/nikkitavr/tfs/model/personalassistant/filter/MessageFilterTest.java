package ru.nikkitavr.tfs.model.personalassistant.filter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.nikkitavr.tfs.model.personalassistant.message.Message;

class MessageFilterTest {

    //TODO: any должен парситься без =value
    @Test
    void testAll() {
        MessageFilter filter = new MessageFilter("{{all}}");
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

        MessageFilter filter = new MessageFilter("{{chat=My Chat}} and {{content~hello}}");
        assertTrue(filter.match(msg));

        filter = new MessageFilter("{{chat=My Chat}} and {{content~bye}}");
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
    void testForwardFromFullVariants() {
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
        filter1 = new MessageFilter("{{forwardFrom=111}} ");
        assertTrue(filter1.match(msg1));
        filter1 = new MessageFilter("{{forwardFrom=Fwd}} ");
        assertTrue(filter1.match(msg1));
        filter1 = new MessageFilter("{{forwardFrom=User}} ");
        assertTrue(filter1.match(msg1));
        filter1 = new MessageFilter("{{forwardFrom=Fwd User}} ");
        assertTrue(filter1.match(msg1));
        filter1 = new MessageFilter("{{forwardFrom=other}} ");
        assertFalse(filter1.match(msg1));
        filter1 = new MessageFilter("{{forwardFrom~fwd}} ");
        assertTrue(filter1.match(msg1));
        filter1 = new MessageFilter("{{forwardFrom~User}} ");
        assertTrue(filter1.match(msg1));
        filter1 = new MessageFilter("{{forwardFrom~other}} ");
        assertFalse(filter1.match(msg1));

        // 2. Forwarded chat + signature
        Chat fwdChat = new Chat();
        fwdChat.setId(222L);
        fwdChat.setTitle("Fwd Channel");
        fwdChat.setUserName("fwdchannel");
        Message msg2 = new Message();
        msg2.setForwardFromChat(fwdChat);
        msg2.setForwardSignature("sig");
        MessageFilter filter2 = new MessageFilter("{{forwardFrom=222}} ");
        assertTrue(filter2.match(msg2));
        filter2 = new MessageFilter("{{forwardFrom=Fwd Channel}} ");
        assertTrue(filter2.match(msg2));
        filter2 = new MessageFilter("{{forwardFrom=fwdchannel}} ");
        assertTrue(filter2.match(msg2));
        filter2 = new MessageFilter("{{forwardFrom=Fwd Channel (sig)}} ");
        assertTrue(filter2.match(msg2));
        filter2 = new MessageFilter("{{forwardFrom~sig}} ");
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
        filter4 = new MessageFilter("{{forwardFrom=333}} ");
        assertTrue(filter4.match(msg4));
        filter4 = new MessageFilter("{{forwardFrom=From}} ");
        assertTrue(filter4.match(msg4));
        filter4 = new MessageFilter("{{forwardFrom=User}} ");
        assertTrue(filter4.match(msg4));
        filter4 = new MessageFilter("{{forwardFrom=From User}} ");
        assertTrue(filter4.match(msg4));
        filter4 = new MessageFilter("{{forwardFrom=other}} ");
        assertFalse(filter4.match(msg4));
        filter4 = new MessageFilter("{{forwardFrom~from}} ");
        assertTrue(filter4.match(msg4));
        filter4 = new MessageFilter("{{forwardFrom~User}} ");
        assertTrue(filter4.match(msg4));
        filter4 = new MessageFilter("{{forwardFrom~other}} ");
        assertFalse(filter4.match(msg4));
    }
} 