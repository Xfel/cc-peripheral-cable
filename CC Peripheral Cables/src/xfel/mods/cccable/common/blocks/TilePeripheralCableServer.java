/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */package xfel.mods.cccable.common.blocks;

public class TilePeripheralCableServer extends TilePeripheralCableCommon{

	protected boolean connectionStateDirty = true;

	@Override
	public void updateEntity() {
		if (connectionStateDirty) {
			updateConnections();
			connectionStateDirty = false;
		}
		updateRoutingTable();
	}

	/**
	 * Immediately disconnects all local peripherals and computers
	 */
	protected void cleanup() {
		connectionStateDirty = true;
	}

	/**
	 * Updates all connections and connects/disconnects peripherals if
	 * necessary.
	 */
	protected void updateConnections() {
		// empty - implemented on server side
	}

	/**
	 * Updates the routing table
	 */
	protected void updateRoutingTable() {
		// empty - implemented on server side
	}
	
}
