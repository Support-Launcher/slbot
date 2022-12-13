package ovh.bricklou.bot.services;

public abstract class IService {
    protected ServiceManager manager;

    public IService(ServiceManager manager) {
        this.manager = manager;
    }

    public boolean onLoad() {
        return true;
    }
    public boolean onStop() {
        return true;
    }
}
