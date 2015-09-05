// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.net.message;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.blay09.mods.eirairc.config.property.NotificationType;

public class MessageNotification implements IMessage {

	private byte typeId;
	private String text;
	
	public MessageNotification() {
	}
	
	public MessageNotification(NotificationType type, String text) {
		this.typeId = (byte) type.ordinal();
		this.text = text;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		typeId = buf.readByte();
		text = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(typeId);
		ByteBufUtils.writeUTF8String(buf, text);
	}

	public byte getNotificationType() {
		return typeId;
	}
	
	public String getText() {
		return text;
	}
}
