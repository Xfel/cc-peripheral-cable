/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.cccable.common.blocks;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet132TileEntityData;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TilePeripheralCableCommon extends TileEntity {

	protected int colorTag;

	protected int connectionState;

	public int getColorTag() {
		return colorTag;
	}

	public void setColorTag(int colorTag) {
		if (this.colorTag == colorTag)
			return;
		
		cleanup();
		this.colorTag = colorTag;

		worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
	}

	public int getConnectionState() {
		return connectionState;
	}

	/**
	 * Immediately disconnects all local peripherals and computers
	 */
	protected void cleanup() {
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		colorTag = nbt.getInteger("Color");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("Color", colorTag);
	}

	@Override
	public Packet getAuxillaryInfoPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("col", (byte) colorTag);
		nbt.setByte("net", (byte) connectionState);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, Packet132TileEntityData pkt) {
		colorTag = pkt.customParam1.getByte("col");
		connectionState = pkt.customParam1.getByte("net");
	}

}
