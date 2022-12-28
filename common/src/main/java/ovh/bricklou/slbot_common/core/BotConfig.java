package ovh.bricklou.slbot_common.core;

import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.SpecNotNull;

import java.util.ArrayList;
import java.util.List;

public class BotConfig {
    @Path("bot.token")
    @SpecNotNull
    private String token;

    @Path("bot.disabled-plugins")
    private ArrayList<String> disabledPlugins;

    public String getToken() {
        return token;
    }

    public List<String> disabledPlugins() {
        if (disabledPlugins == null) {
            return new ArrayList<>();
        }
        return disabledPlugins;
    }
}
