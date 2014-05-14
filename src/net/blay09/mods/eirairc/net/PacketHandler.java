// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.net;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.blay09.mods.eirairc.net.packet.EiraPacket;
import net.blay09.mods.eirairc.net.packet.PacketType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		ByteArrayInputStream bin = new ByteArrayInputStream(packet.data);
		DataInputStream in = new DataInputStream(bin);
		try {
			byte msgType = in.readByte();
			PacketType packetType = PacketType.getPacketType(msgType);
			if(packetType != null) {
				EiraPacket subPacket = packetType.newInstance();
				if(subPacket != null) {
					subPacket.read(in);
					execute(subPacket, manager, player);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void execute(EiraPacket packet, INetworkManager manager, Player player) {
		if(player instanceof EntityPlayerMP) {
			packet.executeServer(manager, (EntityPlayer) player);
		} else {
			packet.executeClient(manager, (EntityPlayer) player);
		}
	}
}
