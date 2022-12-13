package ovh.bricklou.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bot {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);

    public void start() {
        LOGGER.debug("Loading configuration");

        LOGGER.debug("Loading services");

        LOGGER.info("Starting bot !");
    }

    public void shutdown() {
        LOGGER.info("Preparing to shutdown");
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
