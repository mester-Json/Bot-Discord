package dwn.cda.thebot.bot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class Blague extends ListenerAdapter {
    private Guild guild;

    private final String[] blagues = {
            "Pourquoi les plongeurs plongent toujours en arrière et jamais en avant ? Parce que sinon ils tombent toujours dans le bateau.",
            "Pourquoi les poissons détestent l'ordinateur ? Parce qu'ils ont peur du net.",
            "Quel est le comble pour un électricien ? De ne pas être au courant.",
            "Pourquoi les oiseaux ne utilisent jamais de Facebook ? Parce qu'ils ont déjà Twitter.",
            "Quel est le comble pour un électricien ? De ne pas être au courant."
    };

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        guild = event.getGuild();
        guild.updateCommands().addCommands(
                Commands.slash("blague", "Générer une blague aléatoire")
        ).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "blague":
                String blague = obtenirBlagueAleatoire();
                event.reply(blague != null ? blague : "Une erreur est survenue lors de la génération de la blague.").queue();
                break;
            default:
                event.reply("Commande inconnue. Utilisez /blague pour une blague !").setEphemeral(true).queue();
        }
    }

    private String obtenirBlagueAleatoire() {
        if (blagues.length  ==0 ) {
            return "Aucune blague disponible pour le moment.";
        }
        Random random = new Random();
        System.out.println(blagues[random.nextInt(blagues.length)]);
        return blagues[random.nextInt(blagues.length -1)];
    }
}
