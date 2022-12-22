package ovh.bricklou.test_plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ovh.bricklou.slbot_common.plugins.IPlugin;
import ovh.bricklou.slbot_common.plugins.PluginDescriptor;
import ovh.bricklou.slbot_common.services.IPluginManager;

@PluginDescriptor(name = "test-plugin", author = "Bricklou",version = "1.0.0")
public class TestPlugin extends IPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestPlugin.class);

    public TestPlugin(IPluginManager manager) {
        super(manager);
    }

    @Override
    public boolean onLoad() {
        LOGGER.debug("Hi !");

        return true;
    }

    @Override
    public boolean onUnload() {
        LOGGER.debug("Bye !");

        return true;
    }
}
