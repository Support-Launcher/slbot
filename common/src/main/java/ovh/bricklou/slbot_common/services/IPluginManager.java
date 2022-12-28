package ovh.bricklou.slbot_common.services;

public interface IPluginManager {
    boolean syncPluginFolder() throws Exception;

    boolean load(String name);
    boolean unload(String name);

    void loadAll();
    void unloadAll();

    boolean disable(String name) throws Exception;
    boolean enable(String name) throws Exception;
}
