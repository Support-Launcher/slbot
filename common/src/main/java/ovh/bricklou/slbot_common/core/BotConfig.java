package ovh.bricklou.slbot_common.core;

import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.SpecNotNull;

public class BotConfig {
    @Path("bot.token")
    @SpecNotNull
    private String token;

    public String getToken() {
        return token;
    }
}
