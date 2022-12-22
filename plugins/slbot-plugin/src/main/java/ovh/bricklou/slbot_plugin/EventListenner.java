package ovh.bricklou.slbot_plugin;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class EventListenner extends ListenerAdapter {
    @Override
    public void onReady(ReadyEvent event) {
        this.registerCommands(event.getJDA());
    }

    private void registerCommands(JDA jda) {
        Guild guild = jda.getGuildById(842465969469522001L);

        if (guild == null) return;

        guild.updateCommands().addCommands(
                Commands.slash("about", "Affiche les informations du bot")
        ).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("about")) return;

        var embed = new EmbedBuilder()
                .setTitle("À propos")
                .setDescription("Ce bot a pour but de gérer et de simplifier certains processus sur le serveur Discord de Support-Launcher.\n" +
                        "Il va téléverser automatiquement les fichiers sur Hastebin (peut être évité en écrivant `--ignore` dans le message).")
                .setColor(3447003)
                .build();

        var message = new MessageCreateBuilder().addEmbeds(embed).build();
        event.reply(message).queue();
    }
}
