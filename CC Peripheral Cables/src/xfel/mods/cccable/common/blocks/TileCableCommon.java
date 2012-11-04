/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.cccable.common.blocks;

import net.minecraft.src.INetworkManager;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet132TileEntityData;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import xfel.mods.cccable.common.PeripheralCableMod;

/**
 * The part of the cable tile entity that is needed on both client and server side. 
 * 
 * @author Xfel
 *
 */
public class TileCableCommon extends TileEntity {

	/**
	 * The color id or <code>-1</code> if untagged
	 */
	protected int colorTag = -1;

	/**
	 * The connection state
	 * @see ForgeDirection#flag
	 */
	protected int connectionState;

	/**
	 * Returns the color tag of this cable tile.
	 * 
	 * @return a color value or <code>-1</code> if untagged
	 */
	public int getColorTag() {
		return colorTag;
	}

	/**
	 * Sets the color tag value
	 * @param colorTag the new color tag
	 */
	public void setColorTag(int colorTag) {
		if (this.colorTag == colorTag)
			return;

		cleanup();
		this.colorTag = colorTag;

		worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord,
				PeripheralCableMod.cableBlock.blockID);
	}

	/**
	 * Retrieves the connection state
	 * @return the connection state bit mask
	 */
	public int getConnectionState() {
		return connectionState;
	}

	/**
	 * Immediately disconnects the local peripheral. Overridden in subclass
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
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("col", (byte) colorTag);
		nbt.setByte("net", (byte) connectionState);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, nbt);
	}
	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		colorTag = pkt.customParam1.getByte("col");
		connectionState = pkt.customParam1.getByte("net");
		worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
	}

}
