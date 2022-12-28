package ovh.bricklou.slbot_common.services;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ovh.bricklou.slbot_common.core.Configuration;

import java.util.Arrays;

public class JdaService extends IService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdaService.class);
    private JDA jdaInstance;
    private JDABuilder builder;

    public JdaService(ServiceManager manager) {
        super(manager, 50);
    }

    @Override
    public boolean onLoad() {
        LOGGER.debug("Configuring jda builder");
        var config = this.manager.get(Configuration.class);

        this.builder = JDABuilder.createDefault(config.botConfig().getToken())
                .setEnabledIntents(GatewayIntent.getIntents(GatewayIntent.DEFAULT))
                .disableCache(Arrays.asList(CacheFlag.values()));

        return true;
    }

    public void start() {
        LOGGER.debug("Starting bot");
        this.jdaInstance = this.builder.build();
    }

    @Override
    public boolean onStop() {
        if (this.jdaInstance != null) {
            this.jdaInstance.shutdown();
        }

        return true;
    }

    public boolean isBotStarted() {
        return this.jdaInstance != null;
    }

    public JDA instance() {
        return this.jdaInstance;
    }

    public JDABuilder builder() {
        return this.builder;
    }


}
