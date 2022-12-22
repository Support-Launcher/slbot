package ovh.bricklou.slbot_common.plugins;


import ovh.bricklou.slbot_common.services.IPluginManager;
import ovh.bricklou.slbot_common.services.ServiceManager;

public abstract class IPlugin {
    protected final IPluginManager manager;
    protected final ServiceManager serviceManager;

    public IPlugin(IPluginManager manager, ServiceManager serviceManager) {
        this.manager = manager;
        this.serviceManager = serviceManager;
    }

    public boolean onLoad() {
        return true;
    }

    public boolean onUnload() {
        return true;
    }
}
