/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.cccable.common.blocks;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import net.minecraft.item.ItemDye;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import xfel.mods.cccable.api.ICableConnectable;
import xfel.mods.cccable.api.IPeripheralCable;
import xfel.mods.cccable.common.PeripheralAttachment;
import xfel.mods.cccable.common.PeripheralCableMod;
import xfel.mods.cccable.common.routing.IRoutingTableListener;
import xfel.mods.cccable.common.routing.RoutingTable;
import xfel.mods.cccable.common.routing.RoutingTableEntry;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.IPeripheral;

/**
 * 
 * The server-side peripheral cable class. This class contains the logic data.
 * 
 * @author Xfel
 * 
 */
public class TileCableServer extends TileCableCommon implements
		IRoutingTableListener, IPeripheralCable, IPeripheral {

	// true if something next to us changed
	boolean connectionStateDirty = true;

	private RoutingTable routingTable;

	private Map<ForgeDirection, TileCableServer> adjacentCables;

	private IPeripheral localPeripheral;

	private Set<IComputerAccess> localComputers;

	/**
	 * Default constructor
	 */
	public TileCableServer() {
		routingTable = new RoutingTable();

		routingTable.setRoutingTableListener(this);

		adjacentCables = new EnumMap<ForgeDirection, TileCableServer>(
				ForgeDirection.class);
		localComputers = new HashSet<IComputerAccess>();
	}

	@Override
	public void updateEntity() {
		if (localPeripheral instanceof IHostedPeripheral) {
			IHostedPeripheral hp = (IHostedPeripheral) localPeripheral;
			hp.update();
		}
		if (connectionStateDirty) {
			updateConnections();
			connectionStateDirty = false;

			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		updateRoutingTable();
		// System.out.println(routingTable);
	}

	/**
	 * Immediately disconnects the local peripheral
	 */
	protected void cleanup() {
		doDetachPeripheral();
		localPeripheral = null;

		connectionStateDirty = true;
	}

	/**
	 * Updates all connections and connects/disconnects peripherals if
	 * necessary.
	 */
	protected void updateConnections() {
		adjacentCables.clear();
		connectionState = 0;

		IPeripheral newPeripheral = null;
		ForgeDirection peripheralSide = ForgeDirection.UNKNOWN;

		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			int tx = xCoord + dir.offsetX;
			int ty = yCoord + dir.offsetY;
			int tz = zCoord + dir.offsetZ;

			if (ty < 0 || ty > worldObj.getHeight()) {
				continue;
			}

			TileEntity te = worldObj.getBlockTileEntity(tx, ty, tz);

			if (te == null) {
				continue;
			}
			if (te instanceof TileCableServer) {
				TileCableServer cable = (TileCableServer) te;

				if (this.colorTag == -1 || cable.colorTag == -1
						|| this.colorTag == cable.colorTag) {
					adjacentCables.put(dir, cable);
					connectionState |= dir.flag;
				}
			} else {
				IPeripheral tilePeripheral = PeripheralCableMod
						.getPeripheral(te);
				if (colorTag != -1 && newPeripheral == null
						&& tilePeripheral != null) {
					if (tilePeripheral instanceof ICableConnectable
							&& ((ICableConnectable) tilePeripheral)
									.canAttachCableToSide(dir.getOpposite()
											.ordinal())) {
						newPeripheral = tilePeripheral;
						peripheralSide = dir;
						connectionState |= dir.flag;
					} else if (tilePeripheral.canAttachToSide(dir.getOpposite()
							.ordinal())) {
						newPeripheral = tilePeripheral;
						connectionState |= dir.flag;
						peripheralSide = dir;
					}
				} else if (te.getClass().getName()
						.equals("dan200.computer.shared.TileEntityComputer")) {
					connectionState |= dir.flag;
				}
			}
		}

		if (newPeripheral != localPeripheral) {
			doDetachPeripheral();
			localPeripheral = newPeripheral;
			doAttachPeripheral(peripheralSide);
		}
	}

	/**
	 * Updates the routing table
	 */
	protected void updateRoutingTable() {
		for (Map.Entry<ForgeDirection, TileCableServer> entry : adjacentCables
				.entrySet()) {
			routingTable.recieveUpdate(entry.getValue().routingTable,
					entry.getKey());
		}

		routingTable.updateEntries();
	}

	private NBTTagCompound hostedPeripheralStorage;

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		if (nbt.hasKey("HostedPeripheral")) {
			hostedPeripheralStorage = nbt.getCompoundTag("HostedPeripheral");
		} else {
			hostedPeripheralStorage = null;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		if (localPeripheral instanceof IHostedPeripheral) {
			hostedPeripheralStorage = new NBTTagCompound();
			IHostedPeripheral hp = (IHostedPeripheral) localPeripheral;
			hp.writeToNBT(hostedPeripheralStorage);
		} else {
			hostedPeripheralStorage = null;
		}
		nbt.setCompoundTag("HostedPeripheral", hostedPeripheralStorage);
	}

	private void doAttachPeripheral(ForgeDirection peripheralSide) {
		if (localPeripheral != null) {
			if (localPeripheral instanceof ICableConnectable) {
				((ICableConnectable) localPeripheral).attach(this,
						peripheralSide.getOpposite().ordinal(), colorTag);
			}

			if (localPeripheral instanceof IHostedPeripheral
					&& hostedPeripheralStorage != null) {
				IHostedPeripheral hp = (IHostedPeripheral) localPeripheral;
				hp.readFromNBT(hostedPeripheralStorage);
			}

			hostedPeripheralStorage = null;
			synchronized (routingTable) {
				for (RoutingTableEntry entry : routingTable) {
					if (entry.isPeripheralTarget())
						continue;
					try {
						PeripheralAttachment.attachPeripheral(localPeripheral,
								colorTag, entry.getTargetComputer());
					} catch (Exception e) {
						if (!"You are not attached to this Computer".equals(e
								.getMessage())) {
							PeripheralCableMod.MOD_LOGGER.log(Level.WARNING,
									"Error detaching peripheral", e);
						} else {
							PeripheralCableMod.MOD_LOGGER
									.log(Level.INFO,
											"Invalid computer state - not fatal, but also not nice",
											e);
						}
					}
				}
				routingTable.addLocalEntry(new RoutingTableEntry(
						localPeripheral, colorTag));
			}
		}
	}

	private void doDetachPeripheral() {
		if (localPeripheral != null) {
			if (localPeripheral instanceof ICableConnectable) {
				((ICableConnectable) localPeripheral).detach(this);
			}

			synchronized (routingTable) {
				for (RoutingTableEntry entry : routingTable) {
					if (entry.isPeripheralTarget())
						continue;
					try {
						PeripheralAttachment.detachPeripheral(localPeripheral,
								colorTag, entry.getTargetComputer());
					} catch (Exception e) {
						if (!"You are not attached to this Computer".equals(e
								.getMessage())) {
							PeripheralCableMod.MOD_LOGGER.log(Level.WARNING,
									"Error attaching peripheral", e);
						} else {
							PeripheralCableMod.MOD_LOGGER
									.log(Level.INFO,
											"Invalid computer state - not fatal, but also not nice",
											e);
						}
					}
				}
				routingTable.removeLocalEntry(new RoutingTableEntry(
						localPeripheral, colorTag));
			}
		}
	}

	/*
	 * routing table listener hooks
	 */

	@Override
	public void peripheralAdded(RoutingTable routingTable,
			IPeripheral peripheral, int colorTag) {
		for (IComputerAccess computer : localComputers) {
			try {
				PeripheralAttachment.attachPeripheral(peripheral, colorTag,
						computer);
			} catch (Exception e) {
				if (!"You are not attached to this Computer".equals(e
						.getMessage())) {
					PeripheralCableMod.MOD_LOGGER.log(Level.WARNING,
							"Error attaching peripheral", e);
				} else {
					PeripheralCableMod.MOD_LOGGER
							.log(Level.INFO,
									"Invalid computer state - not fatal, but also not nice",
									e);
				}
			}
		}
	}

	@Override
	public void peripheralRemoved(RoutingTable routingTable,
			IPeripheral peripheral, int colorTag) {
		for (IComputerAccess computer : localComputers) {
			try {
				PeripheralAttachment.detachPeripheral(peripheral, colorTag,
						computer);
			} catch (Exception e) {
				if (!"You are not attached to this Computer".equals(e
						.getMessage())) {
					PeripheralCableMod.MOD_LOGGER.log(Level.WARNING,
							"Error detaching peripheral", e);
				} else {
					PeripheralCableMod.MOD_LOGGER
							.log(Level.INFO,
									"Invalid computer state - not fatal, but also not nice",
									e);
				}
			}
		}
	}

	@Override
	public void computerAdded(RoutingTable routingTable,
			IComputerAccess computer) {
		if (localPeripheral != null) {
			try {
				PeripheralAttachment.attachPeripheral(localPeripheral,
						colorTag, computer);
			} catch (Exception e) {
				if (!"You are not attached to this Computer".equals(e
						.getMessage())) {
					PeripheralCableMod.MOD_LOGGER.log(Level.WARNING,
							"Error attaching peripheral", e);
				} else {
					PeripheralCableMod.MOD_LOGGER
							.log(Level.INFO,
									"Invalid computer state - not fatal, but also not nice",
									e);
				}
			}
		}
	}

	@Override
	public void computerRemoved(RoutingTable routingTable,
			IComputerAccess computer) {
		if (localPeripheral != null) {
			try {
				PeripheralAttachment.detachPeripheral(localPeripheral,
						colorTag, computer);
			} catch (Exception e) {
				if (!"You are not attached to this Computer".equals(e
						.getMessage())) {
					PeripheralCableMod.MOD_LOGGER.log(Level.WARNING,
							"Error detaching peripheral", e);
				} else {
					PeripheralCableMod.MOD_LOGGER
							.log(Level.INFO,
									"Invalid computer state - not fatal, but also not nice",
									e);
				}
			}
		}
	}

	/*
	 * IPeriperalCable interface implementation
	 */

	@Override
	public synchronized Map<Integer, IPeripheral> getPeripheralMap() {
		Map<Integer, IPeripheral> result = new HashMap<Integer, IPeripheral>();

		for (RoutingTableEntry entry : routingTable) {
			if (!entry.isPeripheralTarget())
				continue;

			result.put(Integer.valueOf(entry.getId()),
					entry.getTargetPeripheral());
		}
		return result;
	}

	@Override
	public IPeripheral[] getPeripherals() {
		List<IPeripheral> result = new ArrayList<IPeripheral>(16);

		for (RoutingTableEntry entry : routingTable) {
			if (!entry.isPeripheralTarget())
				continue;

			result.add(entry.getTargetPeripheral());
		}
		return (IPeripheral[]) result.toArray(new IPeripheral[result.size()]);
	}

	@Override
	public IPeripheral getPeripheral(int colorTag) {
		RoutingTableEntry entry = routingTable.getPeripheralEntry(colorTag);

		if (entry == null) {
			return null;
		}

		return entry.getTargetPeripheral();
	}

	@Override
	public IComputerAccess[] getComputers() {
		List<IComputerAccess> result = new ArrayList<IComputerAccess>(16);

		for (RoutingTableEntry entry : routingTable) {
			if (entry.isPeripheralTarget())
				continue;

			result.add(entry.getTargetComputer());
		}
		return (IComputerAccess[]) result.toArray(new IComputerAccess[result
				.size()]);
	}

	/*
	 * IPeriperal implementation
	 */

	@Override
	public String getType() {
		return "cable";
	}

	@Override
	public String[] getMethodNames() {
		return new String[] { "isPresent", "getType", "getMethods", "call",
				"list" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, int method,
			Object[] arguments) throws Exception {

		if (method == 4) {// list
			return listPeripherals(computer.getAttachmentSide());
		}

		if (arguments.length < 1 || !(arguments[0] instanceof String)) {
			throw new Exception("invalid color tag");
		}

		int ctag = -1;
		for (int i = 0; i < PeripheralAttachment.colorNames.length; i++) {
			if (ItemDye.dyeColorNames[i].equals(arguments[0])) {
				ctag = i;
				break;
			}
		}
		if (ctag == -1) {
			throw new Exception("invalid color tag " + arguments[0]);
		}

		RoutingTableEntry entry = routingTable.getPeripheralEntry(ctag);

		if (method == 0) {// isPresent
			return new Object[] { Boolean.valueOf(entry != null) };
		}

		if (method == 3 && entry == null) {
			throw new Exception("No peripheral attached");
		}
		if (entry == null)
			return null;

		PeripheralAttachment att = PeripheralAttachment.getComputerWrapper(
				entry.getTargetPeripheral(), ctag, computer);

		switch (method) {
		case 1:// getType
			return new Object[] { att.getType() };
		case 2:// getMethods
			return new Object[] { att.getMethods() };
		case 3:// call

			if ((arguments.length < 2) || (arguments[1] == null)
					|| (!(arguments[1] instanceof String))) {
				throw new Exception("string expected");
			}

			return att.call((String) arguments[1], truncateArray(arguments, 2));
		}
		assert false;
		return null;
	}

	private static Object[] truncateArray(Object[] arguments, int start) {
		if (start >= arguments.length)
			return new Object[0];

		Object[] na = new Object[arguments.length - start];
		System.arraycopy(arguments, 2, na, 0, na.length);

		return na;
	}

	private Object[] listPeripherals(String cside) {
		List<String> result = new ArrayList<String>(16);

		for (RoutingTableEntry entry : routingTable) {
			if (!entry.isPeripheralTarget())
				continue;

			result.add(PeripheralAttachment.getVirtualSide(cside, entry.getId()));
		}
		return result.toArray();
	}

	@Override
	public boolean canAttachToSide(int side) {
		return true;
	}

	@Override
	public void attach(IComputerAccess computer) {
		localComputers.add(computer);

		synchronized (routingTable) {
			for (RoutingTableEntry entry : routingTable) {
				if (!entry.isPeripheralTarget())
					continue;

				PeripheralAttachment.attachPeripheral(
						entry.getTargetPeripheral(), entry.getId(), computer);
			}
			routingTable.addLocalEntry(new RoutingTableEntry(computer));
		}
	}

	@Override
	public void detach(IComputerAccess computer) {
		synchronized (routingTable) {
			for (RoutingTableEntry entry : routingTable) {
				if (!entry.isPeripheralTarget())
					continue;

				PeripheralAttachment.detachPeripheral(
						entry.getTargetPeripheral(), entry.getId(), computer);
			}
			routingTable.removeLocalEntry(new RoutingTableEntry(computer));
		}
	}

	@Override
	public String toString() {
		return routingTable.toString() + "\n Cables at "
				+ adjacentCables.keySet().toString();
	}

}
