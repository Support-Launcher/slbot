package ovh.bricklou.bot.jda;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.slf4j.Logger;
import ovh.bricklou.bot.services.PluginManager;
import ovh.bricklou.bot.services.PluginState;
import ovh.bricklou.slbot_common.core.Configuration;
import ovh.bricklou.slbot_common.services.ServiceManager;

import java.awt.*;
import java.util.function.Function;

public class CommandHandler {
    public static void reloadConfiguration(SlashCommandInteractionEvent event, ServiceManager manager) {
        event.deferReply().setEphemeral(true).queue();

        var config = manager.get(Configuration.class);
        try {
            config.reload();
            createInfoEmbed(event, ":white_check_mark: Success", "Configuration reloaded from file");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void handlePluginCmd(SlashCommandInteractionEvent event, ServiceManager manager, Logger logger) {
        event.deferReply().setEphemeral(true).queue();

        var pluginManager = manager.get(PluginManager.class);
        var nameOption = event.getOption("name");

        if (event.getSubcommandName() == null) {
            createEmbed(event, "Error", "Unknown commands", Color.RED);
            return;
        }

        var name = nameOption != null ? nameOption.getAsString() : null;

        if (name != null && pluginManager.getState(name) == PluginState.NotFound) {
            createEmbed(event, "Plugin Manager", "Unknown plugin `%s`.".formatted(name), Color.RED);
            return;
        }

        switch (event.getSubcommandName()) {
            case "reload" -> {
                if (name != null) {
                    logger.debug("Reload plugin: {}", name);
                    pluginManager.unload(name);
                    pluginManager.load(name);
                    createInfoEmbed(event, "Plugin Manager", "Plugin `%s` reloaded".formatted(name));
                } else {
                    logger.debug("Reload all plugins");
                    pluginManager.unloadAll();
                    pluginManager.loadAll();
                    createInfoEmbed(event, "Plugin Manager", "All plugins reloaded");
                }
            }
            case "load" -> {
                if (name != null) {
                    if (pluginManager.getState(name) == PluginState.Loaded) {
                        createEmbed(event, "Plugin Manager", "Plugin `%s` is already loaded".formatted(name), Color.RED);
                        return;
                    }
                    logger.debug("Load plugin: {}", name);
                    pluginManager.load(name);
                    createInfoEmbed(event, "Plugin Manager", "Plugin `%s` loaded".formatted(name));
                } else {
                    logger.debug("Load all plugins");
                    pluginManager.loadAll();
                    createInfoEmbed(event, "Plugin Manager", "All plugins unloaded");
                }
            }
            case "unload" -> {
                if (name != null) {
                    if (pluginManager.getState(name) == PluginState.Unloaded) {
                        createEmbed(event, "Plugin Manager", "Plugin `%s` is already unloaded".formatted(name), Color.RED);
                        return;
                    }
                    logger.debug("Unload plugin: {}", name);
                    pluginManager.unload(name);
                    createInfoEmbed(event, "Plugin Manager", "Plugin `%s` unloaded".formatted(name));
                } else {
                    logger.debug("Unload all plugins");
                    pluginManager.unloadAll();
                    createInfoEmbed(event, "Plugin Manager", "All plugins unloaded");
                }
            }
            case "state" -> {
                Function<PluginState, String> emoji = (PluginState s) -> switch (s) {
                    case Disabled, Unloaded -> ":red_circle:";
                    case Loaded -> ":green_circle:";
                    case NotFound -> ":warning:";
                };

                if (name != null) {
                    var s = pluginManager.getState(name);
                    createInfoEmbed(event, "Plugin Manager", "%s Plugin `%s` is `%s`".formatted(emoji.apply(s), name, s));
                } else {
                    var plugins = new StringBuilder("**__Plugins states:__**\n");
                    for (var kv : pluginManager.getPluginsState().entrySet()) {
                        plugins.append("%s Plugin `%s` is `%s`\n".formatted(emoji.apply(kv.getValue()), kv.getKey(), kv.getValue()));
                    }
                    createInfoEmbed(event, "Plugin Manager", plugins.toString());
                }
            }
            case "enable" -> {
                if (pluginManager.getState(name) != PluginState.Disabled) {
                    createEmbed(event, "Plugin Manager", "Plugin `%s` is already enabled".formatted(name), Color.RED);
                    return;
                }
                logger.debug("Enable plugin: {}", name);
                try {
                    if (!pluginManager.enable(name)) {
                        createEmbed(event, "Plugin Manager", "Failed to enable plugin \"%s\", please check logs".formatted(name), Color.RED);
                    }
                } catch (Exception e) {
                    logger.error("Failed to enable plugin \"{}\":", name, e);
                    throw new RuntimeException(e);
                }
                createInfoEmbed(event, "Plugin Manager", "Plugin `%s` enabled and loaded".formatted(name));
            }
            case "disable" -> {
                if (pluginManager.getState(name) == PluginState.Disabled) {
                    createEmbed(event, "Plugin Manager", "Plugin `%s` is already disabled".formatted(name), Color.RED);
                    return;
                }
                logger.debug("Disable plugin: {}", name);
                try {
                    if (!pluginManager.disable(name)) {
                        createEmbed(event, "Plugin Manager", "Failed to disable plugin \"%s\", please check logs".formatted(name), Color.RED);
                    }
                } catch (Exception e) {
                    logger.error("Failed to disable plugin \"{}\": ", name, e);
                    throw new RuntimeException(e);
                }
                createInfoEmbed(event, "Plugin Manager", "Plugin `%s` unloaded and disabled".formatted(name));
            }
            default -> createEmbed(event, "Plugin Manager", "Unknown commands", Color.RED);
        }
    }

    private static void createInfoEmbed(SlashCommandInteractionEvent event, String title, String message) {
        createEmbed(event, title, message, new Color(3447003));
    }

    private static void createEmbed(SlashCommandInteractionEvent event, String title, String message, Color color) {
        event.getHook().sendMessage(
                new MessageCreateBuilder().addEmbeds(
                        new EmbedBuilder()
                                .setTitle(title)
                                .setDescription(message)
                                .setColor(color)
                                .build()
                ).build()
        ).setEphemeral(true).queue();
    }
}
