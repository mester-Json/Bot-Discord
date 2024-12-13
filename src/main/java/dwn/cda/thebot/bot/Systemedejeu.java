package dwn.cda.thebot.bot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;
import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

@Component
public class Systemedejeu extends ListenerAdapter {
    private Guild guild;
    private final Map<String, GameSession> gameSessions = new HashMap<>();

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        guild = event.getGuild();
        guild.upsertCommand("rps", "Défiez un autre joueur à Pierre-Feuille-Ciseaux")
                .addOption(USER, "adversaire", "L'adversaire à défier", true)
                .addOption(STRING, "choix", "Votre choix : pierre, feuille ou ciseaux", true)
                .queue();

        guild.upsertCommand("rps_repondre", "Répondez à un défi de Pierre-Feuille-Ciseaux")
                .addOption(STRING, "choix", "Votre choix : pierre, feuille ou ciseaux", true)
                .queue();
    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "rps" -> handleRps(event);
            case "rps_repondre" -> handleResponse(event);
        }
    }

    private void handleRps(SlashCommandInteractionEvent event) {
        OptionMapping adversaireOption = event.getOption("adversaire");
        OptionMapping choixOption = event.getOption("choix");

        if (adversaireOption == null || choixOption == null) {
            event.reply("Veuillez spécifier un adversaire et un choix valide (pierre, feuille ou ciseaux).")
                    .setEphemeral(true).queue();
            return;
        }

        User adversaire = adversaireOption.getAsUser();
        String joueur1Choix = choixOption.getAsString().toLowerCase();

        if (!isValidChoice(joueur1Choix)) {
            event.reply("Choix invalide ! Veuillez choisir entre `pierre`, `feuille` ou `ciseaux`.")
                    .setEphemeral(true).queue();
            return;
        }

        if (adversaire.getId().equals(event.getUser().getId())) {
            event.reply("Vous ne pouvez pas vous défier vous-même !")
                    .setEphemeral(true).queue();
            return;
        }

        String gameId = event.getUser().getId() + "-" + adversaire.getId();
        gameSessions.put(gameId, new GameSession(event.getUser(), adversaire, joueur1Choix));

        event.reply(adversaire.getAsMention() + ", vous avez été défié à un jeu de Pierre-Feuille-Ciseaux par " +
                        event.getUser().getAsMention() + "! Utilisez `/rps_repondre` pour jouer.")
                .queue();
    }

    private void handleResponse(SlashCommandInteractionEvent event) {
        OptionMapping choixOption = event.getOption("choix");

        if (choixOption == null) {
            event.reply("Veuillez spécifier votre choix (pierre, feuille ou ciseaux).").setEphemeral(true).queue();
            return;
        }

        String joueur2Choix = choixOption.getAsString().toLowerCase();

        if (!isValidChoice(joueur2Choix)) {
            event.reply("Choix invalide ! Veuillez choisir entre `pierre`, `feuille` ou `ciseaux`.").setEphemeral(true).queue();
            return;
        }

        User joueur2 = event.getUser();
        String gameId = joueur2.getId() + "-" + event.getUser().getId();
        GameSession session = gameSessions.get(gameId);

        if (session == null) {
            event.reply("Aucun défi en cours avec vous.").setEphemeral(true).queue();
            return;
        }

        String resultat = determineWinner(session.getJoueur1Choix(), joueur2Choix);

        event.reply("**Résultat du jeu !** 🎮\n\n" +
                session.getJoueur1().getAsMention() + " a choisi : **" + session.getJoueur1Choix() + "**\n" +
                session.getJoueur2().getAsMention() + " a choisi : **" + joueur2Choix + "**\n\n" +
                resultat).queue();

        gameSessions.remove(gameId);
    }

    private boolean isValidChoice(String choice) {
        return choice.equals("pierre") || choice.equals("feuille") || choice.equals("ciseaux");
    }

    private String determineWinner(String joueur1Choix, String joueur2Choix) {
        if (joueur1Choix.equals(joueur2Choix)) {
            return "C'est une **égalité** ! 🤝";
        }
        if ((joueur1Choix.equals("pierre") && joueur2Choix.equals("ciseaux")) ||
                (joueur1Choix.equals("feuille") && joueur2Choix.equals("pierre")) ||
                (joueur1Choix.equals("ciseaux") && joueur2Choix.equals("feuille"))) {
            return "🎉 **Le premier joueur a gagné** ! 🏆";
        } else {
            return "🎉 **Le second joueur a gagné** ! 🏆";
        }
    }

    private static class GameSession {
        private final User joueur1;
        private final User joueur2;
        private final String joueur1Choix;

        public GameSession(User joueur1, User joueur2, String joueur1Choix) {
            this.joueur1 = joueur1;
            this.joueur2 = joueur2;
            this.joueur1Choix = joueur1Choix;
        }

        public String getJoueur1Choix() {
            return joueur1Choix;
        }

        public User getJoueur1() {
            return joueur1;
        }

        public User getJoueur2() {
            return joueur2;
        }
    }
}
