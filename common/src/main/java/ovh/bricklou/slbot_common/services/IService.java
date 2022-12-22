package ovh.bricklou.slbot_common.services;

public abstract class IService {
    protected ServiceManager manager;
    // The biggest it is, the latter is start
    protected int priority = 0;

    public IService(ServiceManager manager) {
        this.manager = manager;
    }

    protected IService(ServiceManager manager, int priority) {
        this.manager = manager;
        this.priority = priority;
    }

    public boolean onLoad() {
        return true;
    }

    public boolean onStop() {
        return true;
    }

    public int getPriority() {
        return priority;
    }
}
