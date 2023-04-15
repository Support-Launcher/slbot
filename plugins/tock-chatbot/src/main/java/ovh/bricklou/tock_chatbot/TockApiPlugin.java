package ovh.bricklou.tock_chatbot;

import net.dv8tion.jda.api.requests.GatewayIntent;
import ovh.bricklou.slbot_common.core.Configuration;
import ovh.bricklou.slbot_common.plugins.IPlugin;
import ovh.bricklou.slbot_common.plugins.PluginDescriptor;
import ovh.bricklou.slbot_common.services.IPluginManager;
import ovh.bricklou.slbot_common.services.JdaService;
import ovh.bricklou.slbot_common.services.ServiceManager;
import ovh.bricklou.tock_chatbot.config.PluginConfig;

@PluginDescriptor(name = TockApiPlugin.ID, author = "Bricklou", version = "1.0.0")
public class TockApiPlugin extends IPlugin {
    public static final String ID = "tock-api-plugin";
    private PluginConfig config;

    private MessageListenner listener;

    public TockApiPlugin(IPluginManager manager, ServiceManager serviceManager) {
        super(manager, serviceManager);
    }

    @Override
    public boolean onPreload() {
        var config = this.serviceManager.get(Configuration.class);
        this.config = config.getObject(PluginConfig::new);

        if (this.listener == null) {
            this.listener = new MessageListenner(this);
        }

        JdaService jdaService = this.serviceManager.get(JdaService.class);
        jdaService.builder()
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new MessageListenner(this));

        return true;
    }

    @Override
    public boolean onLoad() {
        var config = this.serviceManager.get(Configuration.class);
        this.config = config.getObject(PluginConfig::new);

        if (this.listener == null) {
            this.listener = new MessageListenner(this);
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

    protected PluginConfig getConfig() {
        return this.config;
    }
}
