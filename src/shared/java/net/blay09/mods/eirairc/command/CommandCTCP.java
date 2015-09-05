package net.blay09.mods.eirairc.command;

import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.MessageFormat;
import net.blay09.mods.eirairc.util.Utils;
import net.blay09.mods.eirairc.wrapper.CommandSender;
import net.blay09.mods.eirairc.wrapper.SubCommandWrapper;
import net.minecraft.command.CommandException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author soniex2
 */
public class CommandCTCP implements SubCommand {

    @Override
    public String getCommandName() {
        return "ctcp";
    }

    @Override
    public String getCommandUsage(CommandSender sender) {
        return "eirairc:commands.ctcp.usage";
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    @Override
    public boolean processCommand(CommandSender sender, IRCContext context, String[] args, boolean serverSide) throws CommandException {
        if (!ConfigHelper.getBotSettings(context).allowPrivateMessages.get()) {
            Utils.sendLocalizedMessage(sender, "commands.ctcp.disabled");
            return true;
        }
        if (args.length < 2) {
            SubCommandWrapper.throwWrongUsageException(this, sender);
        }
        IRCContext target = EiraIRCAPI.parseContext(null, args[0], null);
        if (target.getContextType() == IRCContext.ContextType.Error) {
            Utils.sendLocalizedMessage(sender, target.getName(), args[0]);
            return true;
        } else if (target.getContextType() == IRCContext.ContextType.IRCUser) {
            if (!ConfigHelper.getBotSettings(context).allowPrivateMessages.get()) {
                Utils.sendLocalizedMessage(sender, "commands.ctcp.disabled");
                return true;
            }
        }

        String message = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ").trim();
        if (message.isEmpty()) {
            SubCommandWrapper.throwWrongUsageException(this, sender);
        }
        String format = "{MESSAGE}";
        BotSettings botSettings = ConfigHelper.getBotSettings(target);
        IRCUser botUser = target.getConnection().getBotUser();

        String ircMessage = message;
        if (serverSide) {
            ircMessage = MessageFormat.formatMessage(format, target.getConnection(), target, botUser, message, MessageFormat.Target.IRC, MessageFormat.Mode.Message);
        }
        target.ctcpMessage(ircMessage);

        format = "{MESSAGE}";
        if (target.getContextType() == IRCContext.ContextType.IRCChannel) {
            format = botSettings.getMessageFormat().mcSendChannelMessage;
        } else if (target.getContextType() == IRCContext.ContextType.IRCUser) {
            format = botSettings.getMessageFormat().mcSendPrivateMessage;
        }
        EiraIRCAPI.getChatHandler().addChatMessage(sender, MessageFormat.formatChatComponent(format, target.getConnection(), target, botUser, message, MessageFormat.Target.IRC, MessageFormat.Mode.Message));
        return true;
    }

    @Override
    public boolean canCommandSenderUseCommand(CommandSender sender) {
        return Utils.isOP(sender);
    }

    @Override
    public void addTabCompletionOptions(List<String> list, CommandSender sender, String[] args) {
    }

    @Override
    public boolean isUsernameIndex(String[] args, int idx) {
        return false;
    }

    @Override
    public boolean hasQuickCommand() {
        return false;
    }
}
