/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.cccable.common.routing;

import java.util.HashMap;
import java.util.Iterator;

import net.minecraftforge.common.ForgeDirection;

public class RoutingTable implements Iterable<RoutingTableEntry> {
	private HashMap<RoutingTableEntry, RoutingTableEntry> rtable;

	private IRoutingTableListener listener;

	public RoutingTable() {
		this.rtable = new HashMap<RoutingTableEntry, RoutingTableEntry>();
	}

	public void setRoutingTableListener(IRoutingTableListener listener) {
		this.listener = listener;
	}

	/**
	 * dumps the table to the console
	 */
	public void dump() {
		System.out.println("routing table:");

		for (RoutingTableEntry e : this.rtable.values()) {
			System.out.print(" ");
			System.out.println(e);
		}
		System.out.println();
	}

	@Override
	public String toString() {
		return rtable.keySet().toString();
	}

	@Override
	public Iterator<RoutingTableEntry> iterator() {
		return this.rtable.values().iterator();
	}

	public RoutingTableEntry getComputerEntry(int computerId) {
		return this.rtable.get(new RoutingTableEntry(computerId, false));
	}

	public RoutingTableEntry getPeripheralEntry(int colorTag) {
		return this.rtable.get(new RoutingTableEntry(colorTag, true));
	}

	/**
	 * Adds a local entry whose origin is at our current position
	 * 
	 * @param entry
	 */
	public synchronized void addLocalEntry(RoutingTableEntry entry) {
		entry.distance = 0;
		entry.lifetime = -1;

		this.rtable.put(entry, entry);

		if (listener != null) {
			if (entry.isPeripheralTarget()) {
				listener.peripheralAdded(this, entry.getTargetPeripheral(),
						entry.getId());
			} else {
				listener.computerAdded(this, entry.getTargetComputer());
			}
		}
	}

	/**
	 * Removes a local entry
	 * 
	 * @param entry
	 */
	public synchronized void removeLocalEntry(RoutingTableEntry entry) {
		RoutingTableEntry storedEntry = this.rtable.get(entry);
		if (storedEntry.lifetime != -1) {
			throw new IllegalArgumentException("Not a local entry");
		}
		this.rtable.remove(entry);

		if (listener != null) {
			if (entry.isPeripheralTarget()) {
				listener.peripheralRemoved(this, entry.getTargetPeripheral(),
						entry.getId());
			} else {
				listener.computerRemoved(this, entry.getTargetComputer());
			}
		}
	}

	/**
	 * Updates the routing table from an adjacent one.
	 * 
	 * @param updater
	 *            the table to update from
	 * @param side
	 *            the relative side.
	 */
	public synchronized void recieveUpdate(RoutingTable updater,
			ForgeDirection side) {
		if (updater == this) {
			System.err.println(side);
			throw new IllegalArgumentException("Can't update with yourself");
		}

		synchronized (updater) {
			Iterator<RoutingTableEntry> it = updater.iterator();
			while (it.hasNext()) {
				RoutingTableEntry remoteEntry = (RoutingTableEntry) it.next();
				RoutingTableEntry localEntry = this.rtable.get(remoteEntry);

				if (remoteEntry.side == side.getOpposite()) {
					// don't create loops
					continue;
				}

				if (localEntry == null) {
					// new entry
					localEntry = new RoutingTableEntry(remoteEntry);
					localEntry.side = side;
					this.rtable.put(localEntry, localEntry);

					if (localEntry.isPeripheralTarget()) {
						if (listener != null) {
							listener.peripheralAdded(this,
									localEntry.getTargetPeripheral(),
									localEntry.getId());
						}
					} else {
						if (listener != null) {
							listener.computerAdded(this,
									localEntry.getTargetComputer());
						}
					}
				} else if (localEntry.distance > 0
						&& localEntry.distance > remoteEntry.distance) {
					// update entry
					localEntry.distance = remoteEntry.distance + 1;
					localEntry.side = side;
					localEntry.lifetime = 0;
				}
			}
		}
	}

	/**
	 * update the lifetime of the local entries.
	 */
	public synchronized void updateEntries() {
		Iterator<RoutingTableEntry> it = this.rtable.values().iterator();
		while (it.hasNext()) {
			RoutingTableEntry entry = (RoutingTableEntry) it.next();

			if (!entry.isValid()) {
				it.remove();
				continue;
			}

			if (entry.lifetime >= 0) {
				entry.lifetime++;
				if (entry.lifetime > 3) {
					it.remove();

					if (listener != null) {
						if (entry.isPeripheralTarget()) {
							listener.peripheralRemoved(this,
									entry.getTargetPeripheral(), entry.getId());
						} else {
							listener.computerRemoved(this,
									entry.getTargetComputer());
						}
					}
				}
			}
		}
	}
}