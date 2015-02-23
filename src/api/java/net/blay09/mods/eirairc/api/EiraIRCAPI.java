package net.blay09.mods.eirairc.api;

import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.upload.UploadHoster;
import net.minecraft.command.ICommandSender;

/**
 * Created by Blay09 on 23.02.2015.
 */
public class EiraIRCAPI {

	private static InternalMethods internalMethods;

	/**
	 * INTERNAL METHOD. DO NOT CALL.
	 * @param internalMethods
	 */
	public static void setupAPI(InternalMethods internalMethods) {
		EiraIRCAPI.internalMethods = internalMethods;
	}

	public static void registerSubCommand(SubCommand command) {
		internalMethods.registerSubCommand(command);
	}

	public static void registerUploadHoster(UploadHoster uploadHoster) {
		internalMethods.registerUploadHoster(uploadHoster);
	}

	public static IRCContext parseContext(IRCContext parentContext, String contextPath, IRCContext.ContextType expectedType) {
		return internalMethods.parseContext(parentContext, contextPath, expectedType);
	}

	public static boolean isConnectedTo(String serverHost) {
		return internalMethods.isConnectedTo(serverHost);
	}

	public static boolean hasClientSideInstalled(ICommandSender user) {
		return internalMethods.hasClientSideInstalled(user);
	}

}
