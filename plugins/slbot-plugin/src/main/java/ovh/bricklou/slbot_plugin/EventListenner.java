package ovh.bricklou.slbot_plugin;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;
import ovh.bricklou.slbot_common.core.Configuration;
import ovh.bricklou.slbot_plugin.config.CommandConfig;

import java.awt.*;

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
/*
    private void processBlacklistCommands(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        if (event.getSubcommandName() == null) {
            createEmbed(event, "Error", "Unknown commands", Color.RED);
            return;
        }

        var config = plugin.getConfig();
        var configService = this.plugin.serviceManager.get(Configuration.class);

        switch (event.getSubcommandName()) {
            case "add" -> {
                // Get the user id
                var userOption = event.getOption("user");
                var user = userOption != null ? userOption.getAsUser() : null;
                if (user == null) {
                    createEmbed(event, "Error", "Invalid user", Color.RED);
                    return;
                } else if (user.isBot() || user.isSystem()) {
                    createEmbed(event, "Error", "Can't blacklist a bot or a system user", Color.RED);
                    return;
                } else if (config.blacklist() != null && config.blacklist().contains(user.getId())) {
                    createEmbed(event, "Error", "User already blacklisted", Color.RED);
                    return;
                }

                // Get the reason
                var reasonOption = event.getOption("reason");
                var reason = reasonOption != null ? reasonOption.getAsString() : null;
                if (reason == null) {
                    createEmbed(event, "Error", "Missing reason", Color.RED);
                    return;
                }

                // Get blacklist channel id
                var blacklistChannelId = plugin.getConfig().blacklistChannelId();

                var guild = event.getGuild();
                if (guild == null) {
                    createEmbed(event, "Error", "Can't find the guild", Color.RED);
                    return;
                }

                var channel = guild.getTextChannelById(blacklistChannelId);
                if (channel == null) {
                    createEmbed(event, "Error", "Can't find the blacklist channel", Color.RED);
                    return;
                }

                // Send the message to the blacklist channel
                var message = new MessageCreateBuilder()
                        .addEmbeds(
                                new EmbedBuilder()
                                        .setTitle(String.format("User %s has been blacklisted", user.getGlobalName()))
                                        .addField("ID", user.getId(), true)
                                        .addField("Reason", reason, false)
                                        .setColor(Color.black)
                                        .build()
                        ).build();


                channel
                        .sendMessage(message)
                        .queue((msgResult) -> {
                            var blacklistEntry = new BlacklistConfig();
                            blacklistEntry.reason = reason;
                            blacklistEntry.messageId = msgResult.getId();

                            if (config.blacklist() == null) config.initBlacklist(configService);

                            config.blacklist().set(user.getId(), blacklistEntry);
                            configService.save();

                            createEmbed(event, "Success", "User added to the blacklist", Color.GREEN);
                        });
            }
            case "remove" -> {
                // Get the user id
                var userOption = event.getOption("user");
                var user = userOption != null ? userOption.getAsUser() : null;
                if (user == null) {
                    createEmbed(event, "Error", "Invalid user", Color.RED);
                    return;
                } else if (!config.blacklist().contains(user.getId())) {
                    createEmbed(event, "Error", "User not blacklisted", Color.RED);
                    return;
                }

                // Get blacklist channel id
                var blacklistChannelId = plugin.getConfig().blacklistChannelId();


                var channel = event.getGuild().getTextChannelById(blacklistChannelId);
                if (channel == null) {
                    createEmbed(event, "Error", "Can't find the blacklist channel", Color.RED);
                    return;
                }

                // Get the message in the blacklist channel
                var blacklistEntry = this.getBlacklistEntry(user.getId());
                if (blacklistEntry == null) {
                    createEmbed(event, "Error", "Can't find the blacklist entry", Color.RED);
                    return;
                }

                var message = channel.retrieveMessageById(blacklistEntry.messageId).complete();

                // Delete the message
                message.delete().queue();

                // Remove the user from the blacklist
                this.removeBlacklistEntry(user.getId());
                configService.save();

                createEmbed(event, "Success", "User removed from the blacklist", Color.GREEN);
            }
            case "list" -> {

            }
            default -> createEmbed(event, "Error", "Unknown commands", Color.RED);
        }
    }
*/
    private static void createInfoEmbed(SlashCommandInteractionEvent event, String title, String message) {
        createEmbed(event, title, message, new Color(3447003));
    }

    private static void createEmbed(SlashCommandInteractionEvent event, String title, String message, Color color) {
        createEmbed(event, title, message, color, true);
    }

    private static void createEmbed(SlashCommandInteractionEvent event, String title, String message, Color color, boolean ephemeral) {
        event.getHook().sendMessage(
                new MessageCreateBuilder().addEmbeds(
                        new EmbedBuilder()
                                .setTitle(title)
                                .setDescription(message)
                                .setColor(color)
                                .build()
                ).build()
        ).setEphemeral(ephemeral).queue();
    }
/*
    private BlacklistConfig getBlacklistEntry(String userId) {
        var config = plugin.getConfig();
        if (config.blacklist() == null) return null;
        return config.blacklist().get(userId);
    }

    private void removeBlacklistEntry(String userId) {
        var config = plugin.getConfig();
        if (config.blacklist() == null) return;
        config.blacklist().remove(userId);
        this.plugin.serviceManager.get(Configuration.class).save();
    }

 */
}
