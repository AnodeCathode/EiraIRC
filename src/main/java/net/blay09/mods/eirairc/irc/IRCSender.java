package net.blay09.mods.eirairc.irc;

import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.SocketException;
import java.util.LinkedList;

public class IRCSender implements Runnable {

	private static final String LINE_FEED = "\r\n";

	private final IRCConnectionImpl connection;
	private Thread thread;
	private final LinkedList<String> queue = new LinkedList<String>();
	private boolean running;
	private BufferedWriter writer;

	public IRCSender(IRCConnectionImpl connection) {
		this.connection = connection;
	}

	public void setWriter(BufferedWriter writer) {
		this.writer = writer;
	}

	public void start() {
		if(running) {
			stop();
		}
		running = true;
		thread = new Thread(this, "IRCSender");
		thread.start();
	}

	public boolean addToSendQueue(String message) {
		synchronized (queue) {
			queue.addLast(message);
		}
		return true;
	}

	@Override
	public void run() {
		try {
			while (running) {
				boolean antiFlood = false;
				synchronized (queue) {
					int queueSize = queue.size();
					if (queueSize > 0) {
						int messagesSent = 0;
						while (messagesSent < queueSize && messagesSent < 3) {
							String entry = queue.removeFirst();
							writer.write(entry);
							writer.write(LINE_FEED);
							messagesSent++;
						}
						writer.flush();
						if(messagesSent < queueSize) {
							antiFlood = true;
						}
					}
				}
				try {
					Thread.sleep(antiFlood ? SharedGlobalConfig.antiFloodTime : 100);
				} catch (InterruptedException ignored) {}
			}
		} catch (SocketException e) {
			e.printStackTrace();
			if(connection.isConnected()) {
				connection.tryReconnect();
			}
		} catch (IOException e) {
			e.printStackTrace();
			if(connection.isConnected()) {
				connection.tryReconnect();
			}
		} catch (Exception e) {
			Minecraft.getMinecraft().displayCrashReport(new CrashReport("EiraIRC connection to " + connection.getHost() + " has crashed :(", e));
		}
	}

	public void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException ignored) {
		}
	}
}
