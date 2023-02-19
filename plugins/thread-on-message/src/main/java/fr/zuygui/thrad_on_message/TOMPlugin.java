package fr.zuygui.thrad_on_message;

import fr.zuygui.thrad_on_message.config.PluginConfig;
import net.dv8tion.jda.api.requests.GatewayIntent;
import ovh.bricklou.slbot_common.core.Configuration;
import ovh.bricklou.slbot_common.plugins.IPlugin;
import ovh.bricklou.slbot_common.plugins.PluginDescriptor;
import ovh.bricklou.slbot_common.services.IPluginManager;
import ovh.bricklou.slbot_common.services.JdaService;
import ovh.bricklou.slbot_common.services.ServiceManager;

@PluginDescriptor(name = TOMPlugin.ID, author = "Zuygui", version = "1.0.0")
public class TOMPlugin extends IPlugin {
    public static final String ID = "tom-plugin";
    private PluginConfig config;
    private EventListenner listener;

    public TOMPlugin(IPluginManager manager, ServiceManager serviceManager) {
        super(manager, serviceManager);
    }

    @Override
    public boolean onPreload() {
        var config = this.serviceManager.get(Configuration.class);
        this.config = config.getObject(PluginConfig::new);

        if (this.listener == null) {
            this.listener = new EventListenner(this.config);
        }

        JdaService jdaService = this.serviceManager.get(JdaService.class);
        jdaService.builder()
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(this.listener);

        return true;
    }

    @Override
    public boolean onLoad() {
        var config = this.serviceManager.get(Configuration.class);
        this.config = config.getObject(PluginConfig::new);

        if (this.listener == null) {
            this.listener = new EventListenner(this.config);
        }

        JdaService jdaService = this.serviceManager.get(JdaService.class);
        jdaService.instance().addEventListener(this.listener);

        return true;
    }

    @Override
    public boolean onUnload() {
        if (this.listener != null) {
            JdaService jdaService = this.serviceManager.get(JdaService.class);
            if (jdaService.isBotStarted()) {
                jdaService.instance().removeEventListener(this.listener);
            }
        }

        return true;
    }
}
