package ovh.bricklou.slbot_plugin.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.SpecNotNull;
import ovh.bricklou.slbot_common.core.Configuration;
import ovh.bricklou.slbot_plugin.SLBotPlugin;

import java.util.List;

public class PluginConfig {
    @Path("plugins." + SLBotPlugin.ID + ".guild_id")
    @SpecNotNull
    private long guildId;


    @Path("plugins." + SLBotPlugin.ID + ".commands")
    private List<CommandConfig> commandsList;

    public long guildId() {
        return guildId;
    }

    @Path("plugins." + SLBotPlugin.ID + ".blacklist_channel_id")
    @SpecNotNull
    private long blacklistChannelId;

    public long blacklistChannelId() {
        return blacklistChannelId;
    }

    @Path("plugins." + SLBotPlugin.ID + ".blacklist")
    private Config blacklist;

    public List<CommandConfig> commands() {
        return this.commandsList;
    }
}
