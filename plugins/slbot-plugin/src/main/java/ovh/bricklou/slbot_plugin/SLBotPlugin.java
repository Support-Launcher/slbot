package ovh.bricklou.slbot_plugin;

import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ovh.bricklou.slbot_common.plugins.IPlugin;
import ovh.bricklou.slbot_common.plugins.PluginDescriptor;
import ovh.bricklou.slbot_common.services.IPluginManager;
import ovh.bricklou.slbot_common.services.JdaService;
import ovh.bricklou.slbot_common.services.ServiceManager;

@PluginDescriptor(name = "slbot-plugin", author = "Bricklou", version = "1.0.0")
public class SLBotPlugin extends IPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(SLBotPlugin.class);

    public SLBotPlugin(IPluginManager manager, ServiceManager serviceManager) {
        super(manager, serviceManager);
    }

    @Override
    public boolean onLoad() {
        LOGGER.debug("Hi !");

        JdaService jdaService = this.serviceManager.get(JdaService.class);
        jdaService.builder()
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new EventListenner());


        return true;
    }

    @Override
    public boolean onUnload() {
        LOGGER.debug("Bye !");

        return true;
    }
}
