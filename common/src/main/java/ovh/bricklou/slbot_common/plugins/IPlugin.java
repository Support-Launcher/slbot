package ovh.bricklou.slbot_common.plugins;


import ovh.bricklou.slbot_common.services.IPluginManager;

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
