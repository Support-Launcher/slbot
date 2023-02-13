package ovh.bricklou.cross_post;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventListener extends ListenerAdapter {
    Pattern pattern = Pattern.compile("(?<e1><?)https?://(?<domain>[\\w.]+)/channels/(?<guild>\\d+)/(?<channel>\\d+)/(?<message>\\d+)(?<e2>>?)", Pattern.CASE_INSENSITIVE);

    String[] discordDomains = new String[]{
            // no subdomain
            "discord.com",
            "discordapp.com",
            // public test build
            "ptb.discord.com",
            "ptb.discordapp.com",
            // canary
            "canary.discord.com",
            "canary.discordapp.com",
    };

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            CrossPost.LOGGER.debug("Message from a bot");
            return;
        }

        String content = event.getMessage().getContentRaw();

        Matcher matcher = pattern.matcher(content);

        if (!matcher.matches()) {
            CrossPost.LOGGER.debug("Message doesn't match");
            return;
        }

        try {
            String domain = matcher.group("domain");

            if (domain == null) {
                CrossPost.LOGGER.debug("No domain found");
                return;
            }

            boolean isDiscordDomain = false;

            for (String discordDomain : discordDomains) {
                if (discordDomain.equals(domain)) {
                    isDiscordDomain = true;
                    break;
                }
            }

            if (!isDiscordDomain) {
                CrossPost.LOGGER.debug("Not a discord domain: " + domain);
                return;
            }

            String e1 = matcher.group("e1");
            String e2 = matcher.group("e2");
            if (!e1.isEmpty() && !e2.isEmpty()) {
                CrossPost.LOGGER.debug("Message is escaped");
                return;
            }

            String guildId = matcher.group("guild");
            Guild guild = event.getJDA().getGuildById(guildId);

            if (guild == null || !guild.getId().equals(event.getGuild().getId())) {
                CrossPost.LOGGER.debug("Guild not found or not the same as the current guild");
                return;
            }

            String channelId = matcher.group("channel");
            TextChannel channel = guild.getTextChannelById(channelId);

            if (channel == null) {
                CrossPost.LOGGER.debug("Channel not found");
                return;
            }

            String messageId = matcher.group("message");
            Message message = channel.retrieveMessageById(messageId).complete();

            if (message == null) {
                CrossPost.LOGGER.debug("Message not found");
                return;
            }

            var m = new MessageCreateBuilder();
            var embed = new EmbedBuilder();

            embed.setAuthor(
                    "%s#%s".formatted(message.getAuthor().getName(), message.getAuthor().getDiscriminator()),
                    null,
                    message.getAuthor().getAvatarUrl()
            );
            embed.setDescription(message.getContentRaw());

            embed.setFooter("Cité par " +
                            "%s#%s".formatted(event.getAuthor().getName(), event.getAuthor().getDiscriminator())
                    );
            embed.setTimestamp(Instant.now());

            m.addEmbeds(embed.build());

            event.getMessage().reply(m.build()).queue();
        } catch (Exception ignored) {}
    }
}
