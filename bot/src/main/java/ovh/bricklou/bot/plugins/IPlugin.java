package ovh.bricklou.bot.plugins;

import ovh.bricklou.bot.services.IPluginManager;

public abstract class IPlugin {
    protected final IPluginManager manager;

    public IPlugin(IPluginManager manager) {
        this.manager = manager;
    }

    public boolean onLoad() {
        return true;
    }

    public boolean onUnload() {
        return true;
    }
}
