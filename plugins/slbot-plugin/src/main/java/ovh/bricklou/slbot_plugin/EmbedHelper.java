package ovh.bricklou.slbot_plugin;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.awt.*;

public class EmbedHelper {
    public static void sendEmbed(SlashCommandInteractionEvent event, MessageEmbed embed, boolean ephemeral) {
        event.getHook().sendMessage(
                new MessageCreateBuilder().addEmbeds(embed).build()
        ).setEphemeral(ephemeral).queue();
    }

    public static void sendEmbed(SlashCommandInteractionEvent event, MessageEmbed embed) {
        sendEmbed(event, embed, false);
    }

    public static void sendFormattedEmbed(
        SlashCommandInteractionEvent event,
        String title,
        String description,
        Color color,
        boolean ephemeral
    ) {
        var embed = new EmbedBuilder()
            .setTitle(title)
            .setDescription(description)
            .setColor(color)
            .build();

        sendEmbed(event, embed, ephemeral);
    }
}
