package ovh.bricklou.bot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ovh.bricklou.bot.plugins.Loader;
import ovh.bricklou.slbot_common.core.Configuration;
import ovh.bricklou.slbot_common.plugins.IPlugin;
import ovh.bricklou.slbot_common.plugins.PluginDescriptor;
import ovh.bricklou.slbot_common.services.IPluginManager;
import ovh.bricklou.slbot_common.services.IService;
import ovh.bricklou.slbot_common.services.ServiceManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PluginManager extends IService implements IPluginManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginManager.class);
    private final HashMap<String, IPlugin> plugins = new HashMap<>();
    private final HashMap<String, PluginState> state = new HashMap<>();
    private final HashMap<String, PluginDescriptor> descriptors = new HashMap<>();

    private Configuration config;

    public PluginManager(ServiceManager manager) {
        super(manager);
    }

    @Override
    public boolean onLoad() {
        this.config = this.manager.get(Configuration.class);

        Path pluginsFolder = Path.of("./plugins");
        try {
            if (!Files.exists(pluginsFolder)) {
                Files.createDirectories(pluginsFolder);
            }

            Loader loader = new Loader();

            var stream = Files.list(pluginsFolder);
            for (var pluginPath : stream.toList()) {
                LOGGER.debug("Found: {}", pluginPath.toAbsolutePath());
                if (!pluginPath.toString().endsWith(".jar"))
                    continue;

                try {
                    var pluginClass = loader.loadJar(pluginPath);

                    if (pluginClass == null) continue;

                    PluginDescriptor descriptor = pluginClass.getAnnotation(PluginDescriptor.class);

                    if (this.plugins.containsKey(descriptor.name())) {
                        LOGGER.error("Can't load plugin \"{}\" using \"{}\", the name has already been registered", pluginPath, descriptor.name());
                        continue;
                    }

                    IPlugin plugin = pluginClass.getDeclaredConstructor(IPluginManager.class, ServiceManager.class).newInstance(this, this.manager);

                    this.plugins.put(descriptor.name(), plugin);
                    this.descriptors.put(descriptor.name(), descriptor);
                    this.state.put(descriptor.name(), PluginState.Unloaded);
                } catch (Exception e) {
                    LOGGER.error("Failed to load plugin \"{}\"", pluginPath, e);
                }
            }

            stream.close();
        } catch (Exception e) {
            LOGGER.error("Failed to load plugin manager", e);
            return false;
        }

        return true;
    }

    public void loadAll() {
        List<String> order = generateLoadOrder();

        for (var name : order) {
            if (!load(name)) {
                LOGGER.error("Failed to load plugin \"{}\"", name);
            }
        }
    }

    public void unloadAll() {
        var list = new ArrayList<>(plugins.keySet().stream().toList());
        Collections.reverse(list);
        for (var name : list) {
            if (!unload(name)) {
                LOGGER.error("Failed to unload plugin \"{}\"", name);
            }
        }
    }

    private List<String> generateLoadOrder() {
        List<String> order = new ArrayList<>();
        List<String> toSort = new ArrayList<>(plugins.keySet());

        for (var p : toSort) {
            generateLoadOrderFor(p, order);
        }

        return order;
    }

    private void generateLoadOrderFor(String name, List<String> currentDepsList) {
        PluginDescriptor descriptor = descriptors.get(name);

        for (var d : descriptor.dependencies()) {
            if (!currentDepsList.contains(d)) {
                generateLoadOrderFor(d, currentDepsList);
            }
        }

        if (!currentDepsList.contains(name)) {
            currentDepsList.add(name);
        }
    }

    public boolean load(String name) {
        if (!this.plugins.containsKey(name)) return false;

        // Skip if already loaded
        if (this.state.get(name) == PluginState.Loaded) return true;

        var result = this.plugins.get(name).onLoad();
        if (result) {
            this.state.put(name, PluginState.Loaded);
        }
        return result;
    }

    public boolean unload(String name, boolean disable) {
        if (!this.plugins.containsKey(name)) return false;

        if (this.state.get(name) == PluginState.Unloaded) return true;

        var result = this.plugins.get(name).onUnload();
        if (result) {
            this.state.put(name, PluginState.Unloaded);
        }
        return result;
    }

    public boolean unload(String name) {
        return unload(name, false);
    }

    public HashMap<String, PluginDescriptor> getDescriptors() {
        return descriptors;
    }

    public HashMap<String, IPlugin> getPlugins() {
        return plugins;
    }

    public PluginState getState(String name) {
        var s = this.state.get(name);
        if (s == null) return PluginState.NotFound;
        return s;
    }

    public HashMap<String, PluginState> getPluginsState() {
        return state;
    }
}
