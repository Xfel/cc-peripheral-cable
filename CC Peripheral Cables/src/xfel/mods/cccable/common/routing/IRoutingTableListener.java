/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */package xfel.mods.cccable.common.routing;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;

/**
 * Listens to routing table changes.
 * 
 * @author Xfel
 * @see RoutingTable
 *
 */
public interface IRoutingTableListener {

	/**
	 * Called when a peripheral is added to the routing table
	 * @param routingTable the routing table
	 * @param peripheral the peripheral
	 * @param colorTag the colorTag the peripheral is registered for.
	 */
	void peripheralAdded(RoutingTable routingTable, IPeripheral peripheral,
			int colorTag);

	/**
	 * Called when a peripheral is removed from the routing table
	 * @param routingTable the routing table
	 * @param peripheral the peripheral
	 * @param colorTag the colorTag the peripheral is registered for.
	 */
	void peripheralRemoved(RoutingTable routingTable, IPeripheral peripheral,
			int colorTag);

	/**
	 * Called when a computer is added to the routing table
	 * @param routingTable the routing table
	 * @param computer the computer
	 */
	void computerAdded(RoutingTable routingTable, IComputerAccess computer);

	/**
	 * Called when a computer is removed from the routing table
	 * @param routingTable the routing table
	 * @param computer the computer
	 */
	void computerRemoved(RoutingTable routingTable, IComputerAccess computer);

}
