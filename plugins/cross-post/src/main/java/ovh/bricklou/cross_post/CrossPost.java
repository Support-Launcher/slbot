package ovh.bricklou.cross_post;

import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ovh.bricklou.slbot_common.plugins.IPlugin;
import ovh.bricklou.slbot_common.plugins.PluginDescriptor;
import ovh.bricklou.slbot_common.services.IPluginManager;
import ovh.bricklou.slbot_common.services.JdaService;
import ovh.bricklou.slbot_common.services.ServiceManager;

@PluginDescriptor(name = "cross-post", author = "Bricklou", version = "1.0.0")
public class CrossPost extends IPlugin {
    private EventListener listener;
    protected static final Logger LOGGER = LoggerFactory.getLogger(CrossPost.class);

    public CrossPost(IPluginManager manager, ServiceManager serviceManager) {
        super(manager, serviceManager);
    }

    @Override
    public boolean onPreload() {
        if (this.listener == null) {
            this.listener = new EventListener();
        }

        JdaService jdaService = this.serviceManager.get(JdaService.class);
        jdaService.builder()
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new EventListener());

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
