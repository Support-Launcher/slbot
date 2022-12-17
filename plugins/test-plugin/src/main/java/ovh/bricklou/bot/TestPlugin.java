package ovh.bricklou.bot;

import ovh.bricklou.bot.plugins.IPlugin;
import ovh.bricklou.bot.plugins.PluginDescriptor;
import ovh.bricklou.bot.services.IPluginManager;

@PluginDescriptor(name = "test-plugin", author = "Bricklou",version = "1.0.0")
public class TestPlugin extends IPlugin {
    public TestPlugin(IPluginManager manager) {
        super(manager);
    }

    @Override
    public boolean onLoad() {
        Bot.logger().debug("Hi !");

        return true;
    }

    @Override
    public boolean onUnload() {
        Bot.logger().debug("Bye !");

        return true;
    }
}
