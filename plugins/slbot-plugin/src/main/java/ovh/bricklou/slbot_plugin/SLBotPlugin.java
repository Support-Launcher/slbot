package ovh.bricklou.slbot_plugin;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private EventListenner listener;

    public SLBotPlugin(IPluginManager manager, ServiceManager serviceManager) {
        super(manager, serviceManager);
    }

    @Override
    public boolean onPreload() {
        var config = this.serviceManager.get(Configuration.class);
        this.config = config.getObject(PluginConfig::new);

        if (this.listener == null) {
            this.listener = new EventListenner(this.config);
        }

        JdaService jdaService = this.serviceManager.get(JdaService.class);
        jdaService.builder()
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(this.listener);

        return true;
    }

    @Override
    public boolean onLoad() {
        var config = this.serviceManager.get(Configuration.class);
        this.config = config.getObject(PluginConfig::new);

        if (this.listener == null) {
            this.listener = new EventListenner(this.config);
        }

        JdaService jdaService = this.serviceManager.get(JdaService.class);
        jdaService.instance().addEventListener(this.listener);

        return true;
    }

    @Override
    public boolean onUnload() {
        if (this.listener != null) {
            JdaService jdaService = this.serviceManager.get(JdaService.class);
            if (jdaService.isBotStarted()) {
                jdaService.instance().removeEventListener(this.listener);
            }
        }

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
