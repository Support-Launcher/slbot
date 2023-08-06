package ovh.bricklou.slbot_plugin.modules.blacklist;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import ovh.bricklou.slbot_plugin.EmbedHelper;
import ovh.bricklou.slbot_plugin.SLBotPlugin;

import java.awt.*;

public class BlacklistEventListener extends ListenerAdapter {
    private final DatabaseLayer databaseLayer;
    private final SLBotPlugin plugin;

    public BlacklistEventListener(
            SLBotPlugin plugin,
            DatabaseLayer databaseLayer
    ) {
        this.plugin = plugin;
        this.databaseLayer = databaseLayer;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("blacklist")) {
            return;
        }

        event.deferReply(true).queue();

        if (event.getSubcommandName() == null) {
            EmbedHelper.sendFormattedEmbed(event, "Error", "Unknown commands", Color.RED, true);
            return;
        }

        switch (event.getSubcommandName()) {
            case "add" -> {
                var userOption = event.getOption("user");
                var user = userOption != null ? userOption.getAsUser() : null;

                if (user == null) {
                    EmbedHelper.sendFormattedEmbed(event, "Error", "User not found", Color.RED, true);
                    return;
                } else if (user.isBot() || user.isSystem()) {
                    EmbedHelper.sendFormattedEmbed(event, "Error", "You can't blacklist a bot or a system user", Color.RED, true);
                    return;
                } else if (user.getIdLong() == event.getJDA().getSelfUser().getIdLong()) {
                    EmbedHelper.sendFormattedEmbed(event, "Error", "You can't blacklist the bot", Color.RED, true);
                    return;
                }

                try {
                    var isBL = databaseLayer.isBlacklisted(user.getIdLong());
                    if (isBL) {
                        EmbedHelper.sendFormattedEmbed(event, "Error", "This user is already blacklisted", Color.RED, true);
                        return;
                    }
                } catch (Exception e) {
                    EmbedHelper.sendFormattedEmbed(event, "Error", "An error occurred while blacklisting the user", Color.RED, true);
                    return;
                }

                // Get the reason
                var reasonOption = event.getOption("reason");
                var reason = reasonOption != null ? reasonOption.getAsString() : null;
                if (reason == null) {
                    EmbedHelper.sendFormattedEmbed(event, "Error", "You must specify a reason", Color.RED, true);
                    return;
                }

                // Get blacklist channel id
                var blacklistChannelId = plugin.getConfig().blacklistChannelId();

                var guild = event.getGuild();
                if (guild == null) {
                    EmbedHelper.sendFormattedEmbed(event, "Error", "Can't find the guild", Color.RED, true);
                    return;
                }

                var channel = guild.getTextChannelById(blacklistChannelId);
                if (channel == null) {
                    EmbedHelper.sendFormattedEmbed(event, "Error", "Can't find the blacklist channel", Color.RED, true);
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

                channel.sendMessage(message).queue((msgQueue -> {
                    try {
                        this.databaseLayer.insertUser(
                                user.getIdLong(),
                                msgQueue.getIdLong(),
                                reason
                        );

                        EmbedHelper.sendFormattedEmbed(event, "Success", "User has been blacklisted", Color.GREEN, true);
                    } catch (Exception e) {
                        EmbedHelper.sendFormattedEmbed(event, "Error", "An error occurred while blacklisting the user", Color.RED, true);
                        return;
                    }
                }));
            }
            case "remove" -> {
                // Get the user id
                var userOption = event.getOption("user");
                var user = userOption != null ? userOption.getAsUser() : null;
                if (user == null) {
                    EmbedHelper.sendFormattedEmbed(event, "Error", "User not found", Color.RED, true);
                    return;
                }

                try {
                    var isBL = databaseLayer.isBlacklisted(user.getIdLong());
                    if (!isBL) {
                        EmbedHelper.sendFormattedEmbed(event, "Error", "This user is not blacklisted", Color.RED, true);
                        return;
                    }
                } catch (Exception e) {
                    EmbedHelper.sendFormattedEmbed(event, "Error", "An error occurred while removing the user from the blacklist", Color.RED, true);
                    return;
                }

                // Get the blacklist channel id
                var blacklistChannelId = plugin.getConfig().blacklistChannelId();

                var guild = event.getGuild();
                if (guild == null) {
                    EmbedHelper.sendFormattedEmbed(event, "Error", "Can't find the guild", Color.RED, true);
                    return;
                }

                var channel = guild.getTextChannelById(blacklistChannelId);
                if (channel == null) {
                    EmbedHelper.sendFormattedEmbed(event, "Error", "Can't find the blacklist channel", Color.RED, true);
                    return;
                }

                // Get the message id
                try {
                    var messageId = databaseLayer.getMessageId(user.getIdLong());


                    // Remove the user from the blacklist
                    this.databaseLayer.removeUser(user.getIdLong());

                    var message = channel.retrieveMessageById(messageId).complete();
                    // Delete the message
                    message.delete().queue();

                    EmbedHelper.sendFormattedEmbed(event, "Success", "User has been removed from the blacklist", Color.GREEN, true);
                } catch (Exception e) {
                    EmbedHelper.sendFormattedEmbed(event, "Error", "An error occurred while removing the user from the blacklist", Color.RED, true);
                    return;
                }
            }
            default -> {
                EmbedHelper.sendFormattedEmbed(event, "Error", "Unknown commands", Color.RED, true);
            }
        }
    }
}
