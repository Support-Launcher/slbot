package ovh.bricklou.slbot_plugin;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;
import ovh.bricklou.slbot_plugin.config.CommandConfig;

public class EventListenner extends ListenerAdapter {
    private final SLBotPlugin plugin;

    public EventListenner(SLBotPlugin p) {
        this.plugin = p;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        CommandConfig cmdConfig = null;

        for (var c : plugin.getConfig().commands()) {
            if (c.name.equals(event.getName())) {
                cmdConfig = c;
                break;
            }
        }
        if (cmdConfig == null) return;

        var m = new MessageCreateBuilder();
        if (cmdConfig.embed != null) {
            var embed = new EmbedBuilder();
            embed.setTitle(cmdConfig.embed.title);
            embed.setDescription(cmdConfig.message);

            if (cmdConfig.embed.color != null) {
                embed.setColor(cmdConfig.embed.color);
            }
            m.addEmbeds(embed.build());
        } else {
            m.addContent(cmdConfig.message);
        }

        event.reply(m.build()).queue();
    }
}
