package ovh.bricklou.slbot_common.core;

import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ovh.bricklou.slbot_common.services.IService;
import ovh.bricklou.slbot_common.services.ServiceManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.InvalidPropertiesFormatException;
import java.util.function.Supplier;

public class Configuration extends IService {
    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);
    private FileConfig properties;
    private BotConfig botConfig;

    public Configuration(ServiceManager manager) {
        super(manager);
    }

    public void load() throws Exception {
        var p = Path.of("bot.toml");
        if (!Files.exists(p)) {
            Files.createFile(p);
        }


        if (this.properties == null) {
            this.properties = FileConfig.builder(p).sync().build();
        }
        this.properties.load();

        if (this.properties == null) {
            throw new InvalidPropertiesFormatException("Invalid configuration file");
        }

        this.botConfig = new ObjectConverter().toObject(this.properties, BotConfig::new);
    }

    public BotConfig botConfig() {
        return this.botConfig;
    }

    public <T> T getObject(Supplier<T> supplier) {
        return new ObjectConverter().toObject(this.properties, supplier);
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

    @Override
    public boolean onStop() {
        this.properties.close();
        return true;
    }

    public void reload() throws Exception {
        this.load();
    }

    public void save() {
        this.properties.save();
    }

    public FileConfig getProperties() {
        return properties;
    }
}
