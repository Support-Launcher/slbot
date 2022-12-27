package ovh.bricklou.slbot_plugin;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import ovh.bricklou.slbot_common.core.Configuration;
import ovh.bricklou.slbot_common.plugins.IPlugin;
import ovh.bricklou.slbot_common.plugins.PluginDescriptor;
import ovh.bricklou.slbot_common.services.IPluginManager;
import ovh.bricklou.slbot_common.services.JdaService;
import ovh.bricklou.slbot_common.services.ServiceManager;
import ovh.bricklou.slbot_plugin.config.PluginConfig;

import java.util.ArrayList;
import java.util.List;

@PluginDescriptor(name = SLBotPlugin.ID, author = "Bricklou", version = "1.0.0")
public class SLBotPlugin extends IPlugin {
    public static final String ID = "slbot-plugin";
    private PluginConfig config;

    public SLBotPlugin(IPluginManager manager, ServiceManager serviceManager) {
        super(manager, serviceManager);
    }

    @Override
    public boolean onLoad() {
        var config = this.serviceManager.get(Configuration.class);
        this.config = config.getObject(PluginConfig::new);

        JdaService jdaService = this.serviceManager.get(JdaService.class);
        jdaService.builder()
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new EventListenner(this.config));

        return true;
    }

    @Override
    public List<CommandData> registerCommands() {
        var jdaService = this.serviceManager.get(JdaService.class);
        Guild guild = jdaService.instance().getGuildById(this.config.guildId());

        if (guild == null) return new ArrayList<>();

        // Register all commands
        List<CommandData> cmds = new ArrayList<>();
        for (var cmd : this.config.commands()) {
            cmds.add(Commands.slash(cmd.name, cmd.description));
        }

        guild.updateCommands().addCommands(cmds).queue();

        return new ArrayList<>();
    }
}
