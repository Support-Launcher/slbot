package ovh.bricklou.slbot_plugin;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import ovh.bricklou.slbot_plugin.config.CommandConfig;
import ovh.bricklou.slbot_plugin.config.PluginConfig;

import java.util.HashMap;

public class EventListenner extends ListenerAdapter {
    private final PluginConfig config;
    private final HashMap<String, CommandConfig> commands = new HashMap<>();

    public EventListenner(PluginConfig c) {
        this.config = c;
    }

    @Override
    public void onReady(ReadyEvent event) {
        this.registerCommands(event.getJDA());
    }

    private void registerCommands(JDA jda) {
        Guild guild = jda.getGuildById(this.config.guildId());

        if (guild == null) return;


        // Register all commands
        var updateCommands = guild.updateCommands();
        for (var cmd : this.config.commands()) {
            commands.put(cmd.name, cmd);
            updateCommands.addCommands(Commands.slash(cmd.name, cmd.description));
        }
        updateCommands.queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        var c = commands.get(event.getName());
        if (c == null) return;

        var m = new MessageCreateBuilder();
        if (c.embed != null) {
            var embed = new EmbedBuilder();
            embed.setTitle(c.embed.title);
            embed.setDescription(c.message);

            if (c.embed.color != null) {
                embed.setColor(c.embed.color);
            }
            m.addEmbeds(embed.build());
        } else {
            m.addContent(c.message);
        }

        event.reply(m.build()).queue();
    }
}
