/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.cccable.api;

import java.util.Map;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;

/**
 * This interface is implemented by all cable tile entities. It should not be
 * implemented by your classes.
 * 
 * @author Xfel
 * 
 */
public interface IPeripheralCable {

	/**
	 * Returns the color tag of this cable tile.
	 * 
	 * @return a color value or <code>-1</code> if untagged
	 */
	int getColorTag();

	/**
	 * Retrieves the map of color tags and their peripherals.
	 * 
	 * The returned map is a copy, changing it does not affect the routing table
	 * at all.
	 * 
	 * @return the peripheral map.
	 */
	Map<Integer, IPeripheral> getPeripheralMap();

	/**
	 * Retrieves a list of all peripherals currently known by the routing table.
	 * 
	 * @return a peripheral list
	 */
	IPeripheral[] getPeripherals();

	/**
	 * Retrieves the peripheral addressable under the specified color tag.
	 * 
	 * @param colorTag
	 *            the peripheral's address
	 * @return the peripheral
	 */
	IPeripheral getPeripheral(int colorTag);

	/**
	 * Retrieves a list of all computers currently known by the routing table.
	 * 
	 * Note that if one cable network connects to a computer on multiple sides,
	 * the computer will appear here multiple times. So, you have to be careful
	 * to avoid sending multiple events to one computer.
	 * 
	 * @return a computer list
	 */
	IComputerAccess[] getComputers();

}
