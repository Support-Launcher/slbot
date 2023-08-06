package ovh.bricklou.slbot_plugin.modules.custom_cmds;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import ovh.bricklou.slbot_common.services.JdaService;
import ovh.bricklou.slbot_plugin.SLBotPlugin;
import ovh.bricklou.slbot_plugin.modules.IModule;

import java.util.List;

public class CustomCmds implements IModule {
    @NotNull
    private final SLBotPlugin plugin;

    public CustomCmds(@NotNull SLBotPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public void registerCommands(List<CommandData> commands) {
        for (var cmd : this.plugin.getConfig().commands()) {
            commands.add(Commands.slash(cmd.name, cmd.description));
        }
    }

    @Override
    public void loadListeners(JdaService jdaService) {
        jdaService.builder().addEventListeners(new CustomCmdsEventListener(this.plugin));
    }

    @Override
    public void unload(JdaService jdaService) {
        jdaService.builder().removeEventListeners(new CustomCmdsEventListener(this.plugin));
    }
}
