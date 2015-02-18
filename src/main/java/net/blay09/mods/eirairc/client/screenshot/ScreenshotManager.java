// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.screenshot;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.api.event.RelayChat;
import net.blay09.mods.eirairc.api.upload.IUploadHoster;
import net.blay09.mods.eirairc.api.upload.UploadManager;
import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.config.ScreenshotAction;
import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.config.settings.ThemeColorComponent;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.MessageFormat;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;

public class ScreenshotManager {

	private static ScreenshotManager instance;

	public static void create() {
		instance = new ScreenshotManager();
		instance.load();
	}

	public static ScreenshotManager getInstance() {
		return instance;
	}

	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	private static final Gson gson = new Gson();
	private static IntBuffer intBuffer;
	private static int[] buffer;
	
	private final File screenshotDir = new File(Minecraft.getMinecraft().mcDataDir, "screenshots");
	private final List<Screenshot> screenshots = new ArrayList<Screenshot>();
	private final Comparator<Screenshot> comparator = new Comparator<Screenshot>() {
		@Override
		public int compare(Screenshot first, Screenshot second) {
			long flm = first.getFile().lastModified();
			long slm = second.getFile().lastModified();
			if (flm < slm) {
				return 1;
			} else if(flm > slm) {
				return -1;
			}
			return 0;
		}
	};

	private final List<AsyncUploadScreenshot> uploadTasks = new ArrayList<AsyncUploadScreenshot>();
	private long lastScreenshotScan;
	
	public void load() {
		JsonObject metadataObject;
		try {
			Reader reader = new FileReader(new File(screenshotDir, "eirairc_metadata.json"));
			metadataObject = gson.fromJson(reader, JsonObject.class);
		} catch (FileNotFoundException e) {
			metadataObject = new JsonObject();
		}
		File[] screenshotFiles = screenshotDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String fileName) {
				return fileName.endsWith(".png");
			}
		});
		if (screenshotFiles != null) {
			for (int i = 0; i < screenshotFiles.length; i++) {
				screenshots.add(new Screenshot(screenshotFiles[i], metadataObject.getAsJsonObject(screenshotFiles[i].getName())));
			}
		}
		lastScreenshotScan = System.currentTimeMillis();
		Collections.sort(screenshots, comparator);
	}

	public void save() {
		JsonObject metadataObject = new JsonObject();
		for(int i = 0; i < screenshots.size(); i++) {
			Screenshot screenshot = screenshots.get(i);
			if(screenshot.getMetadata().entrySet().size() > 0) {
				metadataObject.add(screenshot.getFile().getName(), screenshot.getMetadata());
			}
		}
		try {
			JsonWriter writer = new JsonWriter(new FileWriter(new File(screenshotDir, "eirairc_metadata.json")));
			writer.setIndent("  ");
			gson.toJson(metadataObject, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Screenshot takeScreenshot() {
		try {
			int width = Minecraft.getMinecraft().displayWidth;
			int height = Minecraft.getMinecraft().displayHeight;
			int k = width * height;
			if (intBuffer == null || intBuffer.capacity() < k) {
				intBuffer = BufferUtils.createIntBuffer(k);
				buffer = new int[k];
			}
			GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
			intBuffer.clear();
			GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intBuffer);
			intBuffer.get(buffer);
			doSomeCrazyMagic(buffer, width, height);
			BufferedImage bufferedImage = new BufferedImage(width, height, 1);
			bufferedImage.setRGB(0, 0, width, height, buffer, 0, width);
			File screenshotFile = new File(screenshotDir, getScreenshotName(screenshotDir));
			ImageIO.write(bufferedImage, "png", screenshotFile);
			Screenshot screenshot = new Screenshot(screenshotFile, null);
			screenshots.add(screenshot);
			Collections.sort(screenshots, comparator);
			return screenshot;
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}

	private static void doSomeCrazyMagic(int[] buffer, int width, int height) {
		int[] aint1 = new int[width];
		int k = height / 2;

		for (int l = 0; l < k; ++l) {
			System.arraycopy(buffer, l * width, aint1, 0, width);
			System.arraycopy(buffer, (height - 1 - l) * width, buffer, l * width, width);
			System.arraycopy(aint1, 0, buffer, (height - 1 - l) * width, width);
		}
	}

	private static String getScreenshotName(File directory) {
		String s = dateFormat.format(new Date()).toString();
		int i = 1;
		while (true) {
			File file = new File(directory, s + (i == 1 ? "" : "_" + i) + ".png");
			if (!file.exists()) {
				return file.getName();
			}
			i++;
		}
	}

	public List<Screenshot> getScreenshots() {
		return screenshots;
	}

	public void deleteScreenshot(Screenshot screenshot, boolean keepUploaded) {
		screenshot.getFile().delete();
		if(!keepUploaded && screenshot.hasDeleteURL()) {
			Utils.openWebpage(screenshot.getDeleteURL());
		}
		screenshots.remove(screenshot);
	}

	public void uploadScreenshot(Screenshot screenshot, ScreenshotAction followUpAction) {
		IUploadHoster hoster = UploadManager.getUploadHoster(ClientGlobalConfig.screenshotHoster);
		if (hoster != null) {
			uploadTasks.add(new AsyncUploadScreenshot(hoster, screenshot, followUpAction));
		}
	}
	
	public void clientTick(ClientTickEvent event) {
		for(int i = uploadTasks.size() - 1; i >= 0; i--) {
			if(uploadTasks.get(i).isComplete()) {
				AsyncUploadScreenshot task = uploadTasks.remove(i);
				if(task.getScreenshot().isUploaded()) {
					ScreenshotAction action = task.getFollowUpAction();
					if (action == ScreenshotAction.UploadClipboard) {
						Utils.setClipboardString(task.getScreenshot().getUploadURL());
					} else if (action == ScreenshotAction.UploadShare) {
						shareScreenshot(task.getScreenshot());
					}
					save();
				}
			}
		}
	}
	
	public void shareScreenshot(Screenshot screenshot) {
		if(Minecraft.getMinecraft().thePlayer == null) {
			return;
		}
		IRCContext chatTarget = EiraIRC.instance.getChatSessionHandler().getChatTarget();
		String format = ConfigHelper.getBotSettings(chatTarget).getMessageFormat().ircScreenshotUpload;
		format = format.replace("{URL}", screenshot.getUploadURL());
		if(chatTarget == null) {
			format = format.replace("{NICK}", "/me");
			format = format.replace("{USER}", "/me");
			Minecraft.getMinecraft().thePlayer.sendChatMessage(format);
		} else {
			EntityPlayer sender = Minecraft.getMinecraft().thePlayer;
			EnumChatFormatting emoteColor;
			IChatComponent chatComponent;
			if (chatTarget instanceof IRCChannel) {
				BotSettings botSettings = ConfigHelper.getBotSettings(chatTarget);
				emoteColor = ConfigHelper.getTheme(chatTarget).getColor(ThemeColorComponent.emoteTextColor);
				chatComponent = MessageFormat.formatChatComponent(botSettings.getMessageFormat().ircScreenshotUpload, chatTarget, sender, "", MessageFormat.Target.IRC, MessageFormat.Mode.Emote);
			} else if(chatTarget instanceof IRCUser) {
				BotSettings botSettings = ConfigHelper.getBotSettings(chatTarget);
				emoteColor = ConfigHelper.getTheme(chatTarget).getColor(ThemeColorComponent.emoteTextColor);
				chatComponent = MessageFormat.formatChatComponent(botSettings.getMessageFormat().ircScreenshotUpload, chatTarget, sender, "", MessageFormat.Target.IRC, MessageFormat.Mode.Emote);
			} else {
				return;
			}
			if (emoteColor != null) {
				chatComponent.getChatStyle().setColor(emoteColor);
			}
			MinecraftForge.EVENT_BUS.post(new RelayChat(sender, chatComponent.getUnformattedText(), true));
			Utils.addMessageToChat(chatComponent);
		}
	}

	public void handleNewScreenshot(Screenshot screenshot) {
		if (EiraIRC.proxy.isIngame()) {
			ScreenshotAction action = ClientGlobalConfig.screenshotAction;
			if (action == ScreenshotAction.UploadClipboard || action == ScreenshotAction.UploadShare) {
				uploadScreenshot(screenshot, action);
			}
		}
	}

	public void findNewScreenshots(boolean autoAction) {
		File[] screenshotFiles = screenshotDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if(!file.getName().endsWith(".png")) {
					return false;
				}
				if(file.lastModified() > lastScreenshotScan) {
					return true;
				}
				return false;
			}
		});
		if (screenshotFiles != null) {
			for (int i = 0; i < screenshotFiles.length; i++) {
				Screenshot screenshot = new Screenshot(screenshotFiles[i], null);
				if (autoAction) {
					handleNewScreenshot(screenshot);
				}
				screenshots.add(screenshot);
			}
		}
		Collections.sort(screenshots, comparator);
		lastScreenshotScan = System.currentTimeMillis();
	}

}
