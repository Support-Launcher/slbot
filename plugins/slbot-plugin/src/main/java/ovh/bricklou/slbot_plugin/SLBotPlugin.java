package ovh.bricklou.slbot_plugin;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
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
import ovh.bricklou.slbot_plugin.modules.IModule;
import ovh.bricklou.slbot_plugin.modules.blacklist.Blacklist;
import ovh.bricklou.slbot_plugin.modules.custom_cmds.CustomCmds;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@PluginDescriptor(name = SLBotPlugin.ID, author = "Bricklou", version = "1.0.0")
public class SLBotPlugin extends IPlugin {
    private static Logger LOGGER = LoggerFactory.getLogger(SLBotPlugin.class);
    public static final String ID = "slbot-plugin";
    private PluginConfig config;
    private final List<IModule> modules;

    private JdaService jdaService;

    public SLBotPlugin(IPluginManager manager, ServiceManager serviceManager) {
        super(manager, serviceManager);

        this.modules = new ArrayList<>();
        this.jdaService = this.serviceManager.get(JdaService.class);
    }

    private void registerModules() {
        if (this.modules.isEmpty()) {
            this.modules.add(new CustomCmds(this));

            try {
                var module = new Blacklist(this);
                this.modules.add(module);
            } catch (Exception e) {
                LOGGER.error("Failed to initialize Blacklist module", e);
            }
        }

        for (var module : this.modules) {
            module.loadListeners(this.jdaService);
        }
    }

    @Override
    public boolean onPreload() {
        this.jdaService.builder().enableIntents(GatewayIntent.MESSAGE_CONTENT);

        var config = this.serviceManager.get(Configuration.class);
        this.config = config.getObject(PluginConfig::new);

        this.registerModules();

        return true;
    }

    @Override
    public boolean onLoad() {
        var configMgr = this.serviceManager.get(Configuration.class);
        this.config = configMgr.getObject(PluginConfig::new);

        this.registerModules();

        return true;
    }

    @Override
    public boolean onUnload() {
        if (!this.modules.isEmpty()) {
            if (this.jdaService.isBotStarted()) {
                for (var module : this.modules) {
                    module.unload(this.jdaService);
                }
            }
        }

        return true;
    }

    @Override
    public List<CommandData> registerCommands() {
        Guild guild = this.jdaService.instance().getGuildById(this.config.guildId());

        if (guild == null) return new ArrayList<>();

        // Register all commands
        List<CommandData> cmds = new ArrayList<>();
        for (var module : this.modules) {
            module.registerCommands(cmds);
        }

        guild.updateCommands().addCommands(cmds).queue();

        return new ArrayList<>();
    }

    public PluginConfig getConfig() {
        return this.config;
    }
}
