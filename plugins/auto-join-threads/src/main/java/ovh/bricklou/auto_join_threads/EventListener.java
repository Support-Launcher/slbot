package ovh.bricklou.auto_join_threads;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventListener extends ListenerAdapter {
    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
        if (event.getChannelType() != ChannelType.GUILD_PUBLIC_THREAD) return;

        event.getChannel().asThreadChannel().join().queue();
    }
}
