package ovh.bricklou.slbot_plugin.config;

import com.electronwill.nightconfig.core.conversion.SpecNotNull;

public class CommandConfig {
    @SpecNotNull
    public String name;
    @SpecNotNull
    public String description;
    @SpecNotNull
    public String message;

    public Embed embed = null;

    public static class Embed {
        public String title;
        public Integer color = null;
    }
}
