/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */package xfel.mods.cccable.common;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import net.minecraft.src.Facing;

public class RoutingTable implements Iterable {
	private HashMap<Entry, Entry> rtable;
	private IRoutingTableListener listener;

	public RoutingTable() {
		this.rtable = new HashMap<Entry, Entry>();
	}

	public void addLocalEntry(Entry entry) {
		entry.distance = 0;
		entry.lifetime = -1;

		this.rtable.put(entry, entry);

		if (entry.isPeripheralTarget())
			onPeripheralAdded(entry.getTargetPeripheral(), entry.getId());
		else
			onComputerAdded(entry.getTargetComputer(), entry.getComputerSide());
	}

	public void removeLocalEntry(Entry entry) {
		Entry storedEntry = (Entry) this.rtable.get(entry);
		if (storedEntry.lifetime != -1) {
			throw new IllegalArgumentException("Not a local entry");
		}
		this.rtable.put(entry, entry);

		if (entry.isPeripheralTarget())
			onPeripheralRemoved(entry.getTargetPeripheral(), entry.getId());
		else
			onComputerRemoved(entry.getTargetComputer(),
					entry.getComputerSide());
	}

	public void updateEntries() {
		Iterator it = this.rtable.values().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();

			if (!entry.isValid()) {
				it.remove();
				continue;
			}

			if (entry.lifetime >= 0) {
				entry.lifetime++;
				if (entry.lifetime > 3) {
					it.remove();

					if (entry.isPeripheralTarget()) {
						onPeripheralRemoved(entry.getTargetPeripheral(),
								entry.getId());
						continue;
					}

					onComputerRemoved(entry.getTargetComputer(),
							entry.getComputerSide());

					continue;
				}
			}
		}
	}

	public void recieveUpdate(RoutingTable updater, int side) {
		Iterator it = updater.rtable.values().iterator();
		while (it.hasNext()) {
			Entry remoteEntry = (Entry) it.next();
			Entry localEntry = (Entry) this.rtable.get(remoteEntry);

			if (remoteEntry.side == Facing.faceToSide[side]) {
				continue;
			}

			if (localEntry == null) {
				localEntry = new Entry(remoteEntry);
				localEntry.side = side;
				this.rtable.put(remoteEntry, localEntry);

				if (localEntry.isPeripheralTarget()) {
					onPeripheralAdded(localEntry.getTargetPeripheral(),
							localEntry.getId());
				} else {
					onComputerAdded(localEntry.getTargetComputer(),
							localEntry.getComputerSide());
				}
			} else if (localEntry.distance > remoteEntry.distance) {
				localEntry.distance = remoteEntry.distance + 1;
				localEntry.side = side;
				localEntry.lifetime = 0;
			}
		}
	}

	public Entry getComputerEntry(int computerId) {
		return (Entry) this.rtable.get(new Entry(computerId, false));
	}

	public Entry getPeripheralEntry(int colorTag) {
		return (Entry) this.rtable.get(new Entry(colorTag, true));
	}

//	public Iterable getAllComputers() {
//		return new Iterable() {
//			public Iterator iterator() {
//				return new RoutingTable.FilteredIterator(
//						RoutingTable.this.rtable.keySet().iterator(), false);
//			}
//		};
//	}
//
//	public Iterable getAllPeripherals() {
//		return new Iterable() {
//			public Iterator iterator() {
//				return new RoutingTable.FilteredIterator(
//						RoutingTable.this.rtable.keySet().iterator(), true);
//			}
//		};
//	}

	public void setRoutingTableListener(IRoutingTableListener listener) {
		this.listener = listener;
	}

	protected void onPeripheralAdded(IPeripheral peripheral, int colorTag) {
		if (listener != null)
			listener.peripheralAdded(this, peripheral, colorTag);
	}

	protected void onPeripheralRemoved(IPeripheral peripheral, int colorTag) {
		if (listener != null)
			listener.peripheralRemoved(this, peripheral, colorTag);
	}

	protected void onComputerAdded(IComputerAccess computer, String computerSide) {
		if (listener != null)
			listener.computerAdded(this, computer, computerSide);
	}

	protected void onComputerRemoved(IComputerAccess computer,
			String computerSide) {
		if (listener != null)
			listener.computerRemoved(this, computer, computerSide);
	}

	public Iterator iterator() {
		return this.rtable.values().iterator();
	}

	public void dump() {
		System.out.println("routing table:");

		for (Entry e : this.rtable.values()) {
			System.out.print(" ");
			System.out.println(e);
		}
		System.out.println();
	}

//	static class FilteredIterator implements Iterator {
//		private Iterator entries;
//		private boolean filterPeripheral;
//		private Object next;
//
//		FilteredIterator(Iterator entries, boolean filterPeripheral) {
//			this.entries = entries;
//			this.filterPeripheral = filterPeripheral;
//		}
//
//		public boolean hasNext() {
//			while (this.entries.hasNext()) {
//				RoutingTable.Entry nextEntry = (RoutingTable.Entry) this.entries
//						.next();
//				if (nextEntry.isPeripheralTarget() == this.filterPeripheral) {
//					this.next = nextEntry.getTarget();
//					return true;
//				}
//			}
//			return false;
//		}
//
//		public Object next() {
//			if (!hasNext())
//				throw new NoSuchElementException();
//			Object nextVal = this.next;
//			this.next = null;
//			return nextVal;
//		}
//
//		public void remove() {
//			throw new UnsupportedOperationException();
//		}
//	}

	public static class Entry {
		private int side;
		private int distance;
		private int lifetime;
		private Object target;
		private final int id;
		private final boolean peripheral;
		private String cside;

		private Entry(int searchedID, boolean searchPeripheral) {
			this.id = searchedID;
			this.peripheral = searchPeripheral;
		}

		public Entry(IComputerAccess targetComputer) {
			this(targetComputer, null);
		}

		public Entry(IComputerAccess targetComputer, String side) {
			this.target = targetComputer;
			this.id = targetComputer.getID();
			this.cside = side;
			this.peripheral = false;
		}

		public Entry(IPeripheral targetPeripheral, int colortag) {
			this.target = targetPeripheral;
			this.id = colortag;
			this.peripheral = true;
		}

		private Entry(Entry incrementalTarget) {
			this.target = incrementalTarget.target;
			this.id = incrementalTarget.id;
			this.peripheral = incrementalTarget.peripheral;

			incrementalTarget.distance += 1;

			this.cside = incrementalTarget.cside;
		}

		public boolean isPeripheralTarget() {
			return this.peripheral;
		}

		public int getSide() {
			return this.side;
		}

		public int getDistance() {
			return this.distance;
		}

		public boolean isValid() {
			return this.target != null;
		}

		public Object getTarget() {
			if (!isValid())
				throw new IllegalStateException("Invalid entry");
			return this.target;
		}

		public IPeripheral getTargetPeripheral() {
			if (!isValid())
				throw new IllegalStateException("Invalid entry");
			if (!this.peripheral) {
				throw new UnsupportedOperationException(
						"Target is not a peripheral");
			}
			return (IPeripheral) this.target;
		}

		public IComputerAccess getTargetComputer() {
			if (!isValid())
				throw new IllegalStateException("Invalid entry");
			if (this.peripheral) {
				throw new UnsupportedOperationException(
						"Target is not a computer");
			}
			return (IComputerAccess) this.target;
		}

		public String getComputerSide() {
			if (!isValid())
				throw new IllegalStateException("Invalid entry");
			if (this.peripheral) {
				throw new UnsupportedOperationException(
						"Target is not a computer");
			}
			return this.cside;
		}

		public int getId() {
			return this.id;
		}

		public int hashCode() {
			int prime = 31;
			int result = 1;
			result = 31 * result + this.id;
			result = 31 * result + (this.peripheral ? 1231 : 1237);
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Entry other = (Entry) obj;
			if (this.id != other.id) {
				return false;
			}
			return this.peripheral == other.peripheral;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (this.peripheral) {
				IPeripheral target = getTargetPeripheral();

				sb.append("Peripheral ");
				sb.append(target.getType());
				sb.append(" (ID ");
				sb.append(this.id);
				sb.append("): ");
			} else {
				sb.append("Computer ");
				sb.append(this.id);
				sb.append(": ");
			}
			sb.append(this.distance);
			return sb.toString();
		}
	}
}