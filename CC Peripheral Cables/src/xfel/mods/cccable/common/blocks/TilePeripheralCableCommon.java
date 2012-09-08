/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */package xfel.mods.cccable.common.blocks;

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

	public boolean isConnected(ForgeDirection side) {
		return (connectionState & side.flag) != 0;
	}

	/**
	 * Immediately disconnects all local peripherals and computers
	 */
	protected void cleanup() {
	}

}
