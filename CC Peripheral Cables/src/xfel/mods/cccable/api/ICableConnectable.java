/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.cccable.api;

import dan200.computer.api.IPeripheral;

/**
 * This interface allows your peripherals to handle cable connections in a more
 * precise way.
 * 
 * @author Felix
 * 
 */
public interface ICableConnectable extends IPeripheral {

	/**
	 * Asks whether a cable may connect to the given side.
	 * 
	 * This is used in preference of {@link #canAttachToSide(int)} if available.
	 * 
	 * @param side
	 *            The world direction (0=bottom, 1=top, etc) that the cable lies
	 *            relative to the peripheral.
	 * @return Whether to allow the attachment, as a boolean.
	 */
	boolean canAttachCableToSide(int side);

	/**
	 * Called once when a cable is attached. This is independent from computers
	 * attaching.
	 * 
	 * @param cable
	 *            the cable instance
	 * @param side
	 *            The world direction (0=bottom, 1=top, etc) that the cable lies
	 *            relative to the peripheral.
	 * 
	 * @param colorTag
	 *            the color tag this peripheral will be accessible under.
	 */
	void attach(IPeripheralCable cable, int side, int colorTag);

	/**
	 * Called when a cable detaches. This is independent from computer
	 * detaching.
	 * 
	 * @param cable
	 *            the cable instance
	 * @see #attach(IPeripheralCable, int, int)
	 */
	void detach(IPeripheralCable cable);
}
