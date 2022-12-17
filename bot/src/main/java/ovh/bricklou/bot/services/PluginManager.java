package ovh.bricklou.bot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ovh.bricklou.bot.plugins.IPlugin;
import ovh.bricklou.bot.plugins.Loader;
import ovh.bricklou.bot.plugins.PluginDescriptor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class PluginManager extends IService implements IPluginManager {

    private final HashMap<String, IPlugin> plugins = new HashMap<>();
    private final HashMap<String, PluginDescriptor> descriptors = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginManager.class);

    public PluginManager(ServiceManager manager) {
        super(manager);
    }

    @Override
    public boolean onLoad() {
        Path pluginsFolder = Path.of("./plugins");
        try {
            if (!Files.exists(pluginsFolder)) {
                Files.createDirectories(pluginsFolder);
            }

            Loader loader = new Loader();

            for (var pluginPath : Files.list(pluginsFolder).toList()) {
                LOGGER.debug("Found: {}", pluginPath.toAbsolutePath());
                /*if (!pluginPath.endsWith(".jar"))
                    continue;*/

                try {
                    var pluginClass = loader.loadJar(pluginPath);

                    if (pluginClass == null) continue;

                    PluginDescriptor descriptor = pluginClass.getAnnotation(PluginDescriptor.class);
                    IPlugin plugin = pluginClass.getDeclaredConstructor(IPluginManager.class).newInstance(this);

                    this.plugins.put(descriptor.name(), plugin);
                    this.descriptors.put(descriptor.name(), descriptor);
                } catch (Exception e) {
                    LOGGER.error("Failed to load plugin \"{}\"", pluginPath, e);
                }

            }
        } catch (Exception e) {
            LOGGER.error("Failed to load plugin manager", e);
            return false;
        }

        return true;
    }

    public void loadAll() {
        for (IPlugin plugin : plugins.values()) {
            plugin.onLoad();
        }
    }

    public void unloadAll() {
        for (IPlugin plugin : plugins.values()) {
            plugin.onUnload();
        }
    }
}
