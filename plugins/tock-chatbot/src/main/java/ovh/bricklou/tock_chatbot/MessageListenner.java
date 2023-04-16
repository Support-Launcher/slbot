package ovh.bricklou.tock_chatbot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class MessageListenner extends ListenerAdapter {
    private final Logger LOGGER = LoggerFactory.getLogger(MessageListenner.class);
    private final TockApiPlugin plugin;

    private final TockAPIClient tockClient;

    public MessageListenner(TockApiPlugin plugin) {
        this.plugin = plugin;
        this.tockClient = new TockAPIClient(this.plugin.getConfig().tockApiUrl());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();

        ArrayList<String> triggerWords = this.plugin.getConfig().triggerWords();

        // If one of the trigger words is in the message, send it to Tock
        for (String triggerWord : triggerWords) {
            if (!content.toLowerCase().contains(triggerWord.toLowerCase())) continue;

            String response;
            try {
                response = this.tockClient.queryApi(content);
            } catch (InterruptedException | IOException e) {
                LOGGER.error("Failed to query Tock API", e);
                return;
            }

            if (response == null) {
                LOGGER.error("Failed to query Tock API: response is null");
                return;
            }

            if (this.plugin.getConfig().trainMode()) {
                return;
            }

            message.reply(response).queue();
            return;

        }
    }
}
