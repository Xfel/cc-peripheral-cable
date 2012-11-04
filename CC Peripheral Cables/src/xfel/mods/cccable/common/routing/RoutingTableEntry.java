package xfel.mods.cccable.common.routing;

import net.minecraftforge.common.ForgeDirection;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;

/**
 * A routing table entry
 * 
 * @author Xfel
 *
 */
public class RoutingTableEntry {
	ForgeDirection side = ForgeDirection.UNKNOWN;
	int distance;
	int lifetime;
	private Object target;
	private final int id;
	private final boolean peripheral;
	private String cside;

	// constructor for updates
	RoutingTableEntry(RoutingTableEntry incrementalTarget) {
		this.target = incrementalTarget.target;
		this.id = incrementalTarget.id;
		this.peripheral = incrementalTarget.peripheral;

		this.distance = incrementalTarget.distance + 1;

		this.cside = incrementalTarget.cside;
	}

	/**
	 * Creates a new computer entry.
	 * 
	 * @param targetComputer the computer
	 * @param side the side relative to the computer's orientation
	 */
	public RoutingTableEntry(IComputerAccess targetComputer, String side) {
		this.target = targetComputer;
		this.id = targetComputer.getID();
		this.cside = side;
		this.peripheral = false;
	}

	// constructor for search
	RoutingTableEntry(int searchedID, boolean searchPeripheral) {
		this.id = searchedID;
		this.peripheral = searchPeripheral;
	}

	/**
	 * Creates a new peripheral entry.
	 * 
	 * @param targetPeripheral the peripheral
	 * @param colortag the color tag used as target
	 */
	public RoutingTableEntry(IPeripheral targetPeripheral, int colortag) {
		this.target = targetPeripheral;
		this.id = colortag;
		this.peripheral = true;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoutingTableEntry other = (RoutingTableEntry) obj;
		if (this.id != other.id) {
			return false;
		}
		return this.peripheral == other.peripheral;
	}

	/**
	 * @return
	 * @throws IllegalStateException 
	 * @throws UnsupportedOperationException 
	 */
	public String getComputerSide() throws IllegalStateException, UnsupportedOperationException {
		if (!isValid())
			throw new IllegalStateException("Invalid entry");
		if (this.peripheral) {
			throw new UnsupportedOperationException("Target is not a computer");
		}
		return this.cside;
	}

	/**
	 * @return
	 */
	public int getDistance() {
		return this.distance;
	}

	/**
	 * Returns the entry id. For computer entries, this is the computer id. For peripehral entries, this is the color tag.
	 * @return
	 */
	public int getId() {
		return this.id;
	}

	public ForgeDirection getSide() {
		return this.side;
	}

	public Object getTarget() {
		if (!isValid())
			throw new IllegalStateException("Invalid entry");
		return this.target;
	}

	public IComputerAccess getTargetComputer() {
		if (!isValid())
			throw new IllegalStateException("Invalid entry");
		if (this.peripheral) {
			throw new UnsupportedOperationException("Target is not a computer");
		}
		return (IComputerAccess) this.target;
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

	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = 31 * result + this.id;
		result = 31 * result + (this.peripheral ? 1231 : 1237);
		return result;
	}

	public boolean isPeripheralTarget() {
		return this.peripheral;
	}

	public boolean isValid() {
		return this.target != null;
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