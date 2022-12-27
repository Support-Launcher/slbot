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

public class CommandHandler {
    public static void reloadConfiguration(ServiceManager manager) {
        var config = manager.get(Configuration.class);
        try {
            config.reload();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void handlePluginCmd(SlashCommandInteractionEvent event, ServiceManager manager, Logger logger) {
        event.deferReply().setEphemeral(true).queue();

        var pluginManager = manager.get(PluginManager.class);
        var nameOption = event.getOption("name");

        if (event.getSubcommandName() == null) {
            createEmbed(event, "Unknown commands", Color.RED);
            return;
        }

        var name = nameOption != null ? nameOption.getAsString() : null;

        if (name != null && pluginManager.getState(name) == PluginState.NotFound) {
            createEmbed(event, "Unknown plugin `%s`.".formatted(name), Color.RED);
            return;
        }

        switch (event.getSubcommandName()) {
            case "reload" -> {
                if (name != null) {
                    logger.debug("Reload plugin: {}", name);
                    pluginManager.unload(name);
                    pluginManager.load(name);
                    createInfoEmbed(event, "Plugin `%s` reloaded".formatted(name));
                } else {
                    logger.debug("Reload all plugins");
                    pluginManager.unloadAll();
                    pluginManager.loadAll();
                    createInfoEmbed(event, "All plugins reloaded");
                }
            }
            case "load" -> {
                if (name != null) {
                    if (pluginManager.getState(name) == PluginState.Loaded) {
                        createEmbed(event, "Plugin `%s` is already loaded".formatted(name), Color.RED);
                        return;
                    }
                    logger.debug("Load plugin: {}", name);
                    pluginManager.load(name);
                    createInfoEmbed(event, "Plugin `%s` loaded".formatted(name));
                } else {
                    logger.debug("Load all plugins");
                    pluginManager.loadAll();
                    createInfoEmbed(event, "All plugins unloaded");
                }
            }
            case "unload" -> {
                if (name != null) {
                    if (pluginManager.getState(name) == PluginState.Unloaded) {
                        createEmbed(event, "Plugin `%s` is already unloaded".formatted(name), Color.RED);
                        return;
                    }
                    logger.debug("Unload plugin: {}", name);
                    pluginManager.unload(name);
                    createInfoEmbed(event, "Plugin `%s` unloaded".formatted(name));
                } else {
                    logger.debug("Unload all plugins");
                    pluginManager.unloadAll();
                    createInfoEmbed(event, "All plugins unloaded");
                }
            }
            case "state"-> {
                if (name != null) {
                    var s = pluginManager.getState(name);
                    var emoji = switch (s) {
                        case Loaded -> ":green_circle:";
                        case Unloaded -> ":red_circle:";
                        case NotFound -> ":warning:";
                    };
                    createInfoEmbed(event, "%s Plugin `%s` is `%s`".formatted(emoji, name, s));
                } else {
                    var plugins = new StringBuilder("**__Plugins states:__**\n");
                    for (var kv : pluginManager.getPluginsState().entrySet()) {
                        var emoji = switch (kv.getValue()) {
                            case Loaded -> ":green_circle:";
                            case Unloaded -> ":red_circle:";
                            case NotFound -> ":warning:";
                        };
                        plugins.append("%s Plugin `%s` is `%s`\n".formatted(emoji, kv.getKey(), kv.getValue()));
                    }
                    createInfoEmbed(event, plugins.toString());
                }
            }
            default -> {
                createEmbed(event, "Unknown commands", Color.RED);
            }
        }
    }

    private static void createInfoEmbed(SlashCommandInteractionEvent event, String message) {
        createEmbed(event, message, new Color(3447003));
    }

    private static void createEmbed(SlashCommandInteractionEvent event, String message, Color color) {
        event.getHook().sendMessage(
                new MessageCreateBuilder().addEmbeds(
                        new EmbedBuilder()
                                .setTitle("Plugin Manager")
                                .setDescription(message)
                                .setColor(color)
                                .build()
                ).build()
        ).setEphemeral(true).queue();
    }
}
