package ovh.bricklou.test_plugin;

import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ovh.bricklou.slbot_common.plugins.IPlugin;
import ovh.bricklou.slbot_common.plugins.PluginDescriptor;
import ovh.bricklou.slbot_common.services.IPluginManager;
import ovh.bricklou.slbot_common.services.JdaService;
import ovh.bricklou.slbot_common.services.ServiceManager;

@PluginDescriptor(name = "test-plugin", author = "Bricklou", version = "1.0.0")
public class TestPlugin extends IPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestPlugin.class);

    public TestPlugin(IPluginManager manager, ServiceManager serviceManager) {
        super(manager, serviceManager);
    }

    @Override
    public boolean onLoad() {
        LOGGER.debug("Hi !");

        JdaService jdaService = this.serviceManager.get(JdaService.class);
        jdaService.builder()
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new MessageListenner());

        return true;
    }

    @Override
    public boolean onUnload() {
        LOGGER.debug("Bye !");

        return true;
    }
}
