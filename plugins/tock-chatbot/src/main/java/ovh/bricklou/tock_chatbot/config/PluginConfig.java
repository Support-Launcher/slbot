package ovh.bricklou.tock_chatbot.config;

import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.SpecNotNull;
import ovh.bricklou.tock_chatbot.TockApiPlugin;

import java.util.ArrayList;

public class PluginConfig {
    @Path("plugins." + TockApiPlugin.ID + ".tock_api_url")
    @SpecNotNull
    private String tockApiUrl;

    @Path("plugins." + TockApiPlugin.ID + ".tock_api_token")
    @SpecNotNull
    private String tockApiToken;

    @Path("plugins." + TockApiPlugin.ID + ".trigger_words")
    private ArrayList<String> triggerWords;

    public String tockApiUrl() {
        return tockApiUrl;
    }

    public String tockApiToken() {
        return tockApiToken;
    }

    public ArrayList<String> triggerWords() {
        if (triggerWords == null) {
            return new ArrayList<>();
        }
        return triggerWords;
    }
}
