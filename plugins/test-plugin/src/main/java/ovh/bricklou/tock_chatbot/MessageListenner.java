package ovh.bricklou.tock_chatbot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageListenner extends ListenerAdapter {
    private final Logger LOGGER = LoggerFactory.getLogger(MessageListenner.class);

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();

        if (content.equals("!ping")) {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("Pong!").queue();
        }
    }
}
