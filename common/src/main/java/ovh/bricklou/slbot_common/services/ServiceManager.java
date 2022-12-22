package ovh.bricklou.slbot_common.services;

import java.util.*;

public class ServiceManager {

    private final Map<String, IService> services = new HashMap<>();

    public ServiceManager() {
    }

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
        var services = new java.util.ArrayList<>(this.services.values());
        services.sort(Comparator.comparingInt(IService::getPriority));

        for (var s : services) {
            if (!s.onLoad()) {
                return false;
            }
        }

        return true;
    }

    public void unloadAll() {
        var services = new java.util.ArrayList<>(this.services.values());
        services.sort(Comparator.comparingInt(IService::getPriority));
        Collections.reverse(services);

        for (var s : services) {
            s.onStop();
        }
    }

}
