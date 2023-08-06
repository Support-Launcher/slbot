package ovh.bricklou.slbot_common.plugins;


import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import ovh.bricklou.slbot_common.services.IPluginManager;
import ovh.bricklou.slbot_common.services.ServiceManager;

import java.util.ArrayList;
import java.util.List;

public abstract class IPlugin {
    protected final IPluginManager manager;
    public final ServiceManager serviceManager;

    public IPlugin(IPluginManager manager, ServiceManager serviceManager) {
        this.manager = manager;
        this.serviceManager = serviceManager;
    }

    public boolean onPreload() {
        return true;
    }

    public boolean onLoad() {
        return true;
    }

    public boolean onUnload() {
        return true;
    }

    public List<CommandData> registerCommands() {
        return new ArrayList<>();
    }
}
