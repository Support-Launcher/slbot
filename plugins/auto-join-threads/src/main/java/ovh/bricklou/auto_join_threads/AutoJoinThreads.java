package ovh.bricklou.auto_join_threads;

import net.dv8tion.jda.api.requests.GatewayIntent;
import ovh.bricklou.slbot_common.plugins.IPlugin;
import ovh.bricklou.slbot_common.plugins.PluginDescriptor;
import ovh.bricklou.slbot_common.services.IPluginManager;
import ovh.bricklou.slbot_common.services.JdaService;
import ovh.bricklou.slbot_common.services.ServiceManager;

@PluginDescriptor(name = "auto-join-threads", author = "Bricklou", version = "1.0.0")
public class AutoJoinThreads extends IPlugin {
    private EventListener listener;

    public AutoJoinThreads(IPluginManager manager, ServiceManager serviceManager) {
        super(manager, serviceManager);
    }

    @Override
    public boolean onPreload() {
        if (this.listener == null) {
            this.listener = new EventListener();
        }

        JdaService jdaService = this.serviceManager.get(JdaService.class);
        jdaService.builder()
                .addEventListeners(new EventListener());

        return true;
    }

    @Override
    public boolean onLoad() {
        if (this.listener == null) {
            this.listener = new EventListener();
        }

        JdaService jdaService = this.serviceManager.get(JdaService.class);
        jdaService.instance().addEventListener(this.listener);

        return true;
    }

    @Override
    public boolean onUnload() {
        JdaService jdaService = this.serviceManager.get(JdaService.class);
        if (jdaService.isBotStarted()) {
            jdaService.instance().removeEventListener(this.listener);
        }

        return true;
    }
}
