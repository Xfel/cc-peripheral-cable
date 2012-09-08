/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */package xfel.mods.cccable.common.routing;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;

public interface IRoutingTableListener {

	void peripheralAdded(RoutingTable routingTable, IPeripheral peripheral,
			int colorTag);

	void peripheralRemoved(RoutingTable routingTable, IPeripheral peripheral,
			int colorTag);

	void computerAdded(RoutingTable routingTable, IComputerAccess computer,
			String computerSide);

	void computerRemoved(RoutingTable routingTable, IComputerAccess computer,
			String computerSide);

}
