package ovh.bricklou.slbot_common.services;

import java.util.HashMap;
import java.util.Map;

public class ServiceManager {

    private final Map<String, IService> services = new HashMap<>();

    public ServiceManager() {}

    public <T extends IService> void register(Class<T> ServiceClass) throws Exception {
        if (this.services.containsKey(ServiceClass.getName())) {
            throw new ServiceAlreadyExistsException("Service " + ServiceClass.getName() + " has already been instantiated");
        }

        T c = ServiceClass.getDeclaredConstructor(ServiceManager.class).newInstance(this);
        this.services.put(ServiceClass.getName(), c);
    }

    public <T extends IService> T get(Class<T> sClass) {
        return sClass.cast(this.services.get(sClass.getName()));
    }

    public boolean loadAll() {
        for (var s : this.services.values()) {
            if (!s.onLoad()) {
                return false;
            }
        }

        return true;
    }

    public void unloadAll() {
        for (var s : this.services.values()) {
            s.onStop();
        }
    }

    public void boot() {
    }
}
