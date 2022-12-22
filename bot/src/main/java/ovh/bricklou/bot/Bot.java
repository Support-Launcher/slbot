package ovh.bricklou.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ovh.bricklou.slbot_common.services.JdaService;
import ovh.bricklou.bot.services.PluginManager;
import ovh.bricklou.slbot_common.services.ServiceManager;
import ovh.bricklou.slbot_common.core.Configuration;

public class Bot {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);

    private final ServiceManager serviceManager = new ServiceManager();

    public void start() throws Exception {
        LOGGER.debug("Loading services");
        this.serviceManager.register(Configuration.class);
        this.serviceManager.register(PluginManager.class);
        this.serviceManager.register(JdaService.class);

        if (!this.serviceManager.loadAll()) {
            return;
        }

        LOGGER.info("Loading plugins !");
        PluginManager manager = this.serviceManager.get(PluginManager.class);
        manager.loadAll();

        LOGGER.info("Configuring bot !");
        JdaService jdaService = this.serviceManager.get(JdaService.class);
        jdaService.start();
    }

    public void shutdown() {
        LOGGER.info("Preparing to shutdown");

        PluginManager manager = this.serviceManager.get(PluginManager.class);
        manager.unloadAll();

        this.serviceManager.unloadAll();
    }

    public static Logger logger() {
        return LOGGER;
    }
}
