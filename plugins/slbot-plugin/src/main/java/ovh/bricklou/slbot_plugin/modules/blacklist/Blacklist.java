package ovh.bricklou.slbot_plugin.modules.blacklist;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ovh.bricklou.slbot_common.services.JdaService;
import ovh.bricklou.slbot_plugin.SLBotPlugin;
import ovh.bricklou.slbot_plugin.modules.IModule;

import java.sql.SQLException;
import java.util.List;

public class Blacklist implements IModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(Blacklist.class);
    private final DatabaseLayer databaseLayer;
    private final SLBotPlugin plugin;

    public Blacklist(SLBotPlugin plugin) throws Exception {
        databaseLayer = new DatabaseLayer();
        this.plugin = plugin;
    }

    @Override
    public void registerCommands(List<CommandData> commands) {
        commands.add(
                Commands.context(Command.Type.USER, "Blacklist user")
                        .setName("blacklist")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS))
        );
        commands.add(
                Commands.slash("blacklist", "Blacklist user")
                        .addSubcommands(
                                new SubcommandData("add", "Add user to blacklist")
                                        .addOption(OptionType.USER, "user", "User to blacklist", true)
                                        .addOption(OptionType.STRING, "reason", "Reason of the blacklist", true),
                                new SubcommandData("remove", "Remove user from blacklist")
                                        .addOption(OptionType.USER, "user", "User to remove from blacklist", true),
                                new SubcommandData("list", "List all blacklisted users")
                        )
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS))
        );
    }

    @Override
    public void unload(JdaService jdaService) {
        jdaService.builder().removeEventListeners(new BlacklistEventListener(this.plugin, this.databaseLayer));
        try {
            this.databaseLayer.closeConnection();
        } catch (SQLException e) {
            LOGGER.error("Failed to close database connection", e);
        }
    }

    @Override
    public void loadListeners(JdaService jdaService) {
        jdaService.builder().addEventListeners(new BlacklistEventListener(this.plugin, this.databaseLayer));
    }
}
