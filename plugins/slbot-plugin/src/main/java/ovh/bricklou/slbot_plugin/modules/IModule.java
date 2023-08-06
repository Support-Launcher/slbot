package ovh.bricklou.slbot_plugin.modules;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import ovh.bricklou.slbot_common.services.JdaService;

import java.util.List;

public interface IModule {
    public void registerCommands(List<CommandData> commands);
    public void unload(JdaService jdaService);

    void loadListeners(JdaService jdaService);
}
