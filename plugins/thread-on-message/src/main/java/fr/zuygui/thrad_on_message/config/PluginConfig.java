package fr.zuygui.thrad_on_message.config;

import fr.zuygui.thrad_on_message.TOMPlugin;
import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.SpecNotNull;
import java.util.ArrayList;

public class PluginConfig {
    @Path("plugins." + TOMPlugin.ID + ".guild_id")
    @SpecNotNull
    private long guildId;

    public long guildId() {
        return guildId;
    }

    @Path("plugins." + TOMPlugin.ID + ".channels-ids")
    @SpecNotNull
    private ArrayList<Long> channelsIds;

    public ArrayList<Long> channelsIds() {
        return channelsIds;
    }
}