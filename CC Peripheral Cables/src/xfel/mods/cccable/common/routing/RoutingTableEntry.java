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

	// constructor for updates
	RoutingTableEntry(RoutingTableEntry incrementalTarget) {
		this.target = incrementalTarget.target;
		this.id = incrementalTarget.id;
		this.peripheral = incrementalTarget.peripheral;

		this.distance = incrementalTarget.distance + 1;
	}

	/**
	 * Creates a new computer entry.
	 * 
	 * @param targetComputer
	 *            the computer
	 */
	public RoutingTableEntry(IComputerAccess targetComputer) {
		this.target = targetComputer;
		this.id = targetComputer.getID();
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
	 * @param targetPeripheral
	 *            the peripheral
	 * @param colortag
	 *            the color tag used as target
	 */
	public RoutingTableEntry(IPeripheral targetPeripheral, int colortag) {
		this.target = targetPeripheral;
		this.id = colortag;
		this.peripheral = true;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RoutingTableEntry other = (RoutingTableEntry) obj;
		if (this.id != other.id) {
			return false;
		}
		return this.peripheral == other.peripheral;
	}

	/**
	 * @return the distance from the entry's origin in tiles.
	 */
	public int getDistance() {
		return this.distance;
	}

	/**
	 * Returns the entry id. For computer entries, this is the computer id. For
	 * peripehral entries, this is the color tag.
	 * 
	 * @return the entry id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Returns the side this entry was created from. It is also the direction in
	 * which the shortest known way can be found.
	 * 
	 * @return the source direction or {@link ForgeDirection#UNKNOWN} if the
	 *         entry was created here.
	 */
	public ForgeDirection getSide() {
		return this.side;
	}

	/**
	 * Returns the entry target. This can be a computer or a peripheral.
	 * 
	 * @return the target.
	 * @see #isPeripheralTarget()
	 */
	public Object getTarget() {
		return this.target;
	}

	/**
	 * Returns the target computer.
	 * 
	 * @return the target computer
	 * @throws UnsupportedOperationException
	 *             if the target isn't a computer.
	 * @see #isPeripheralTarget()
	 * @see #getTarget()
	 */
	public IComputerAccess getTargetComputer() {
		if (this.peripheral) {
			throw new UnsupportedOperationException("Target is not a computer");
		}
		return (IComputerAccess) this.target;
	}

	/**
	 * Returns the target peripheral.
	 * 
	 * @return the target peripheral
	 * @throws UnsupportedOperationException
	 *             if the target isn't a peripheral.
	 * @see #isPeripheralTarget()
	 * @see #getTarget()
	 */
	public IPeripheral getTargetPeripheral() {
		if (!this.peripheral) {
			throw new UnsupportedOperationException(
					"Target is not a peripheral");
		}
		return (IPeripheral) this.target;
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = 31 * result + this.id;
		result = 31 * result + (this.peripheral ? 1231 : 1237);
		return result;
	}

	/**
	 * Checks whetehr the traget is a peripheral or a computer
	 * 
	 * @return <code>true</code> if the target is a peripheral,
	 *         <code>false</code> if it is a computer.
	 */
	public boolean isPeripheralTarget() {
		return this.peripheral;
	}

	@Override
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