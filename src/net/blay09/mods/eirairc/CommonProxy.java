// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc;

import net.blay09.mods.eirairc.net.packet.PacketNotification;
import net.blay09.mods.eirairc.util.NotificationType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;

public class CommonProxy {

	public void setupClient() {
	}

	public void sendNotification(EntityPlayerMP entityPlayer, NotificationType type, String text) {
		Packet packet = new PacketNotification(type, text).createPacket();
		if(packet != null) {
			entityPlayer.playerNetServerHandler.sendPacketToPlayer(packet);
		}
	}
	
	public void publishNotification(NotificationType type, String text) {
		Packet packet = new PacketNotification(type, text).createPacket();
		if(packet != null) {
			MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(packet);
		}
	}
	
	public String getUsername() {
		return null;
	}

	public boolean isIngame() {
		return true;
	}
	
	public void renderTick(float delta) {
	}

}
