package net.blay09.mods.eirairc.command.extension;

import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.command.SubCommand;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.DisplayConfig;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.irc.IRCConnection;
import net.blay09.mods.eirairc.irc.IRCTarget;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class CommandColor extends SubCommand {

	private static final String COLOR_NONE = "none";
	
	@Override
	public String getCommandName() {
		return "color";
	}

	@Override
	public String getUsageString(ICommandSender sender) {
		return "irc.commands.color";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCTarget context, String[] args, boolean serverSide) {
		if(!(sender instanceof EntityPlayer)) {
			return true;
		}
		if(!DisplayConfig.enableNameColors) {
			Utils.sendLocalizedMessage(sender, "irc.color.disabled");
			return true;
		}
		if(args.length < 1) {
			throw new WrongUsageException(Utils.getLocalizedMessage("irc.commands.color"));
		}
		String colorName = args[1].toLowerCase();
		if(!Utils.isOP(sender) && (GlobalConfig.colorBlackList.contains(colorName) || DisplayConfig.opColor.equals(colorName))) {
			Utils.sendLocalizedMessage(sender, "irc.color.blackList", colorName);
			return true;
		}
		if(!colorName.equals(COLOR_NONE) && !Utils.isValidColor(colorName)) {
			Utils.sendLocalizedMessage(sender, "irc.color.invalid", colorName);
			return true;
		}
		EntityPlayer entityPlayer = (EntityPlayer) sender;
		NBTTagCompound persistentTag = entityPlayer.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		NBTTagCompound tagCompound = persistentTag.getCompoundTag(Globals.NBT_EIRAIRC);
		if(colorName.equals(COLOR_NONE)) {
			tagCompound.removeTag(Globals.NBT_NAMECOLOR);
		} else {
			tagCompound.setString(Globals.NBT_NAMECOLOR, colorName);
		}
		persistentTag.setTag(Globals.NBT_EIRAIRC, tagCompound);
		entityPlayer.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentTag);
		if(colorName.equals(COLOR_NONE)) {
			Utils.sendLocalizedMessage(sender, "irc.color.reset");
		} else {
			Utils.sendLocalizedMessage(sender, "irc.color.set", colorName);
		}
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return Utils.isOP(sender);
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {
		list.add(COLOR_NONE);
		Utils.addValidColorsToList(list);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int idx) {
		return false;
	}

	@Override
	public boolean hasQuickCommand() {
		return true;
	}

}
