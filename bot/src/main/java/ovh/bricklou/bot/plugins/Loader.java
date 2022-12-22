package ovh.bricklou.bot.plugins;

import ovh.bricklou.bot.Bot;
import ovh.bricklou.slbot_common.plugins.IPlugin;
import ovh.bricklou.slbot_common.plugins.PluginDescriptor;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Loader {
    public Loader() {

    }

    public Class<? extends IPlugin> loadJar(Path p) throws IOException {
        Collection<Class<?>> classes = new ArrayList<>();

        JarFile jar = new JarFile(p.toAbsolutePath().toFile());

        var ucl = this.addClassesToClassLoader(p.toUri().toURL());

        for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
            JarEntry entry = entries.nextElement();
            String file = entry.getName();

            if (file.endsWith(".class")) {
                String classname = file.substring(0, file.lastIndexOf('.')).replace('/', '.');

                try {
                    var c = ucl.loadClass(classname);
                    classes.add(c);
                } catch (Throwable e) {
                    Bot.logger().error("WARNING: failed to instantiate {} from {}", classname, file, e);
                }
            }
        }

        ucl.close();

        for (Class<?> aClass : classes) {
            if (!aClass.isAnnotationPresent(PluginDescriptor.class)) {
                continue;
            }

            return aClass.asSubclass(IPlugin.class);
        }

        return null;
    }

    private URLClassLoader addClassesToClassLoader(URL aClass) {
        return new URLClassLoader(new URL[]{aClass}, ClassLoader.getSystemClassLoader());
    }
}
