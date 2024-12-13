package dwn.cda.thebot.bot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class Systemedejeu extends ListenerAdapter {


        private Guild guild;



        public void onGuildReady(@NotNull GuildReadyEvent event) {
            guild = event.getGuild();
            guild.updateCommands().addCommands(
                    Commands.slash("pizza", "Miam")
            ).queue();
        }

        public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
            switch (event.getName()) {
                case "pizza":
                    event.reply("Miam").queue();
                    break;
                default:
                    event.reply("I'm a teapot").setEphemeral(true).queue();
            }
        }
    }
