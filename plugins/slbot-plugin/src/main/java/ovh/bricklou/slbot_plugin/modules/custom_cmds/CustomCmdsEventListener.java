package ovh.bricklou.slbot_plugin.modules.custom_cmds;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ovh.bricklou.slbot_common.services.ServiceManager;
import ovh.bricklou.slbot_plugin.SLBotPlugin;
import ovh.bricklou.slbot_plugin.config.CommandConfig;

public class CustomCmdsEventListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomCmdsEventListener.class);
    private final SLBotPlugin plugin;

    public CustomCmdsEventListener(SLBotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
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
