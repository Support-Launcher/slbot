package fr.zuygui.thrad_on_message;
import fr.zuygui.thrad_on_message.config.PluginConfig;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class EventListenner extends ListenerAdapter {
    private final PluginConfig config;

    public EventListenner(PluginConfig c) {
        this.config = c;
    }

    public void onMessageInteraction(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.isFromType(ChannelType.PRIVATE)) return;

        if (event.getGuild().equals(config.guildId())) {
            if (config.channelsIds().contains(event.getChannel())) {
                event.getMessage().createThreadChannel("Retours");
            }
        }
    }
}