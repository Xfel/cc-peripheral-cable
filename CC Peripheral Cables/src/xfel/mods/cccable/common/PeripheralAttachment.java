package xfel.mods.cccable.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.minecraft.src.ItemDye;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;

/**
 * An attachment of a specific peripheral to a specific computer on a specific address.
 * 
 * Also used as security wrapper for the {@link IComputerAccess} passed from the computer
 * 
 * @author Xfel
 *
 */
public class PeripheralAttachment implements IComputerAccess {
	private IPeripheral peripheral;

	private int colorTag;

	private IComputerAccess computer;

	private String cside;

	private HashSet<String> myMounts;

	private boolean attached;

	private HashMap<String, Integer> methodMap;
	private String[] methods;
	private String type;

	PeripheralAttachment(IPeripheral peripheral, int colorTag,
			IComputerAccess computer, String cside) {
		super();
		this.peripheral = peripheral;
		this.colorTag = colorTag;
		this.computer = computer;
		this.cside = cside;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + colorTag;
		result = prime * result + ((computer == null) ? 0 : System.identityHashCode(computer));
		result = prime * result + ((cside == null) ? 0 : cside.hashCode());
		result = prime * result
				+ ((peripheral == null) ? 0 : peripheral.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PeripheralAttachment other = (PeripheralAttachment) obj;
		if (colorTag != other.colorTag)
		if (computer != other.computer)
			return false;
		if (cside == null) {
			if (other.cside != null)
				return false;
		} else if (!cside.equals(other.cside))
			return false;
		if (peripheral == null) {
			if (other.peripheral != null)
				return false;
		} else if (!peripheral.equals(other.peripheral))
			return false;
		return true;
	}

	// does the attach op
	void attach() {
//		System.out.println("attach "+this);
		type = peripheral.getType();
		methods = peripheral.getMethodNames();
		methodMap = new HashMap<String, Integer>();

		for (int i = 0; i < methods.length; i++) {
			methodMap.put(methods[i], Integer.valueOf(i));
		}

		myMounts = new HashSet<String>();
		attached = true;

		peripheral.attach(computer, getVirtualSide(cside, colorTag));
		computer.queueEvent("peripheral_attach",
				new Object[] { getVirtualSide(cside, colorTag) });
	}

	// does the detach op
	void detach() {
//		System.out.println("detach "+this);
		peripheral.detach(computer);

		for (String loc : myMounts) {
			computer.unmount(loc);
		}

		myMounts = null;
		attached = false;

		computer.queueEvent("peripheral_detach",
				new Object[] { getVirtualSide(cside, colorTag) });
	}

	public int createNewSaveDir(String subPath) {
		if (!attached) {
			throw new RuntimeException("You are not attached to this Computer");
		}
		return computer.createNewSaveDir(subPath);
	}

	public String mountSaveDir(String desiredLocation, String subPath, int id,
			boolean readOnly) {
		if (!attached) {
			throw new RuntimeException("You are not attached to this Computer");
		}
		String dir = computer.mountSaveDir(desiredLocation, subPath, id,
				readOnly);
		myMounts.add(dir);
		return dir;
	}

	public String mountSaveDir(String desiredLocation, String subPath, int id,
			boolean readOnly, long spaceLimit) {
		if (!attached) {
			throw new RuntimeException("You are not attached to this Computer");
		}
		String dir = computer.mountSaveDir(desiredLocation, subPath, id,
				readOnly, spaceLimit);
		myMounts.add(dir);
		return dir;
	}

	public String mountFixedDir(String desiredLocation, String path,
			boolean readOnly) {
		if (!attached) {
			throw new RuntimeException("You are not attached to this Computer");
		}
		String dir = computer.mountFixedDir(desiredLocation, path, readOnly);
		myMounts.add(dir);
		return dir;
	}

	public String mountFixedDir(String desiredLocation, String path,
			boolean readOnly, long spaceLimit) {
		if (!attached) {
			throw new RuntimeException("You are not attached to this Computer");
		}
		String dir = computer.mountFixedDir(desiredLocation, path, readOnly,
				spaceLimit);
		myMounts.add(dir);
		return dir;
	}

	public void unmount(String location) {
		if (!attached) {
			throw new RuntimeException("You are not attached to this Computer");
		}
		if (!myMounts.remove(location)) {
			throw new RuntimeException("You didn't mount this location");
		}
		computer.unmount(location);
	}

	public int getID() {
		if (!attached) {
			throw new RuntimeException("You are not attached to this Computer");
		}
		return computer.getID();
	}

	public void queueEvent(String event) {
		if (!attached) {
			throw new RuntimeException("You are not attached to this Computer");
		}
		computer.queueEvent(event);
	}

	public void queueEvent(String event, Object[] arguments) {
		if (!attached) {
			throw new RuntimeException("You are not attached to this Computer");
		}
		computer.queueEvent(event, arguments);
	}

	/**
	 * Call a peripehral method...
	 * @param methodName
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public Object[] call(String methodName, Object[] args) throws Exception {
		assert (this.attached == true);
		if (this.methodMap.containsKey(methodName)) {
			int method = ((Integer) this.methodMap.get(methodName)).intValue();
			
			return this.peripheral.callMethod(this, method, args);
		}
		throw new Exception("No such method " + methodName);
	}

	/**
	 * Retrieve the peripheral method table.
	 * @return
	 */
	public Map<Integer, String> getMethods() {
		Map<Integer, String> table = new HashMap<Integer, String>();
		for (int i = 0; i < methods.length; i++) {
			table.put(Integer.valueOf(i + 1), methods[i]);
		}
		return table;
	}

	/**
	 * Returns the peripheral type.
	 * @return
	 */
	public String getType() {
		return type;
	}

	public static String getVirtualSide(String side, int colorTag) {
		StringBuilder sb = new StringBuilder(side);
		sb.append(':');
		sb.append(ItemDye.dyeColorNames[colorTag]);
		return sb.toString();
	}

	private static HashMap<PeripheralAttachment, PeripheralAttachment> attachments = new HashMap<PeripheralAttachment, PeripheralAttachment>();

	public static synchronized void attachPeripheral(IPeripheral peripheral,
			int colorTag, IComputerAccess computer, String computerSide) {
		PeripheralAttachment att = new PeripheralAttachment(peripheral,
				colorTag, computer, computerSide);

		if (!attachments.containsKey(att)) {
			attachments.put(att, att);

			att.attach();
		}
	}

	public static synchronized void detachPeripheral(IPeripheral peripheral,
			int colorTag, IComputerAccess computer, String computerSide) {
		PeripheralAttachment att = new PeripheralAttachment(peripheral,
				colorTag, computer, computerSide);

		if (attachments.containsKey(att)) {
			attachments.remove(att).detach();
		}
	}

	public static synchronized PeripheralAttachment getComputerWrapper(
			IPeripheral peripheral, int colorTag, IComputerAccess computer,
			String computerSide) {
		PeripheralAttachment att = new PeripheralAttachment(peripheral,
				colorTag, computer, computerSide);
		
		return attachments.get(att);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PeripheralAttachment [peripheral=");
		builder.append(peripheral.getClass().getName());
		builder.append(", colorTag=");
		builder.append(colorTag);
		builder.append(", computer=");
		builder.append(computer.getID());
		builder.append(", cside=");
		builder.append(cside);
		builder.append(", methods=");
		builder.append(Arrays.toString(methods));
		builder.append("]");
		return builder.toString();
	}
}
