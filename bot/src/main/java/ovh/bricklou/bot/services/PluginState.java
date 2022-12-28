package ovh.bricklou.bot.services;

public enum PluginState {
    Disabled("Disabled"),
    Loaded("Loaded"),
    Unloaded("Unloaded"),
    NotFound("Not found");

    private final String state;

    PluginState(String name) {
        this.state = name;
    }


    @Override
    public String toString() {
        return this.state;
    }
}
