package ovh.bricklou.bot.jda;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ovh.bricklou.bot.services.PluginManager;
import ovh.bricklou.slbot_common.services.JdaService;
import ovh.bricklou.slbot_common.services.ServiceManager;

import java.util.ArrayList;
import java.util.List;

public class EventListenner extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventListenner.class);
    private final ServiceManager manager;

    public EventListenner(ServiceManager manager) {
        this.manager = manager;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        var pluginsManager = this.manager.get(PluginManager.class);

        List<CommandData> commandDataList = new ArrayList<>(this.registerBuiltinCommands());
        for (var p : pluginsManager.getPlugins().values()) {
            commandDataList.addAll(p.registerCommands());
        }

        var jdaService = this.manager.get(JdaService.class);
        LOGGER.debug("Registering bot application commands");
        jdaService.instance().updateCommands().addCommands(commandDataList).queue();
    }

    private List<CommandData> registerBuiltinCommands() {
        List<SubcommandData> subcmds = new ArrayList<>(List.of(
                new SubcommandData("reload", "Reload a plugin"),
                new SubcommandData("load", "Load a plugin"),
                new SubcommandData("unload", "Unload a plugin"),
                new SubcommandData("state", "Get plugin state")
        ));

        for (var s : subcmds) {
            s.addOption(OptionType.STRING, "name", "Name of the plugin", false, true);
        }

        subcmds.addAll(List.of(
                new SubcommandData("enable", "Enable a plugin")
                        .addOption(OptionType.STRING, "name", "Name of the plugin", true, true),
                new SubcommandData("disable", "Disable a plugin")
                        .addOption(OptionType.STRING, "name", "Name of the plugin", true, true),
                new SubcommandData("sync", "Sync plugins folder")));

        List<CommandData> cmds = List.of(
                Commands.slash("reload-config", "Reload the bot configurations"),
                Commands.slash("plugins", "Manage plugins").addSubcommands(subcmds)
        );

        for (var c : cmds) {
            c.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS));
        }

        return cmds;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        LOGGER.debug("slashcommand \"{}\" issued", event.getName());
        switch (event.getName()) {
            case "plugins" -> CommandHandler.handlePluginCmd(event, this.manager, LOGGER);
            case "reload-config" -> CommandHandler.reloadConfiguration(event, this.manager);
            default -> {
            }
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("plugins")) {
            if (event.getFocusedOption().getName().equals("name")) {
                var pluginManager = this.manager.get(PluginManager.class);
                List<Command.Choice> options = pluginManager.getDescriptors().keySet().stream().filter(word -> word.startsWith(event.getFocusedOption().getValue())).map(word -> new Command.Choice(word, word)).toList();
                event.replyChoices(options).queue();
            }
        }
    }
}
