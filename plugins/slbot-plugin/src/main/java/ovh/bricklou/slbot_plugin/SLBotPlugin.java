package ovh.bricklou.slbot_plugin;

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

@PluginDescriptor(name = SLBotPlugin.ID, author = "Bricklou", version = "1.0.0")
public class SLBotPlugin extends IPlugin {
    public static final String ID = "slbot-plugin";

    public SLBotPlugin(IPluginManager manager, ServiceManager serviceManager) {
        super(manager, serviceManager);
    }

    @Override
    public boolean onLoad() {
        var config = this.serviceManager.get(Configuration.class);
        var c = config.getObject(PluginConfig::new, "plugins.%s".formatted(ID));

        JdaService jdaService = this.serviceManager.get(JdaService.class);
        jdaService.builder()
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new EventListenner(c));


        return true;
    }
}
