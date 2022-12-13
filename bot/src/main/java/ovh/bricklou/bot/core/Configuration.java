package ovh.bricklou.bot.core;

import ovh.bricklou.bot.Bot;
import ovh.bricklou.bot.services.IService;
import ovh.bricklou.bot.services.ServiceManager;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class Configuration extends IService {
    private final Properties properties = new Properties();

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
            Bot.getLogger().debug("Loading configuration file");
            this.load();
        } catch (Exception e) {
            Bot.getLogger().error("Failed to load configuration: ", e);
            return false;
        }

        return true;
    }
}
