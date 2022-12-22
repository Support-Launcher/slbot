package ovh.bricklou.bot.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ovh.bricklou.slbot_common.services.ServiceManager;
import ovh.bricklou.slbot_common.services.IService;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class Configuration extends IService {
    private final Properties properties = new Properties();

    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);
    public Configuration(ServiceManager manager) {
        super(manager);
    }

    public void load() throws Exception {
        var p = Path.of("bot.properties");
        if (!Files.exists(p)) {
            Files.createFile(p);
        }

        FileReader reader = new FileReader(p.toFile());

        this.properties.load(reader);

        if (!this.properties.containsKey("token")) {
            throw new InvalidPropertiesFormatException("Bot token not configured");
        }
    }

    public String getToken() {
        return this.properties.getProperty("token");
    }

    @Override
    public boolean onLoad() {
        try {
            LOGGER.debug("Loading configuration file");
            this.load();
        } catch (Exception e) {
            LOGGER.error("Failed to load configuration: ", e);
            return false;
        }

        return true;
    }
}
