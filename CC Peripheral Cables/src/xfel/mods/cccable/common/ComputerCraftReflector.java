package xfel.mods.cccable.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import dan200.computer.api.IPeripheral;
import dan200.computer.api.IPeripheralHandler;
import net.minecraft.tileentity.TileEntity;

/**
 * This class contains all sorts of reflection "hacks" used to access non-exposed computercraft functions.
 * 
 * @author Xfel
 *
 */
public class ComputerCraftReflector {

	private static Method method_getPeripheralFromClass;

	private static Class<?> class_IComputerEntity;

	/**
	 * Returns the peripheral for the given tile entity.
	 * 
	 * If the tile entity implements {@link IPeripheral} itself, then it is
	 * returned. Otherwise, this method uses the corresponding {@link IPeripheralHandler}
	 * if available.
	 * 
	 * @param te the tile entity
	 * @return the peripheral or <code>null</code> if the tile is none.
	 */
	public static IPeripheral getPeripheral(TileEntity te) {
		if (te instanceof IPeripheral) {
			return (IPeripheral) te;
		}
		if (ComputerCraftReflector.method_getPeripheralFromClass != null) {
			IPeripheralHandler handler = null;
			try {
				handler = (IPeripheralHandler) ComputerCraftReflector.method_getPeripheralFromClass
						.invoke(null, te.getClass());
			} catch (IllegalAccessException e) {
				// as we did setAccessible(true), this can't happen
				PeripheralCableMod.MOD_LOGGER.log(Level.SEVERE, "This should not happen?!?", e);
			} catch (InvocationTargetException e) {
				PeripheralCableMod.MOD_LOGGER.log(Level.WARNING,
						"Error retrieving the peripheral handler for class "
								+ te.getClass().getSimpleName(), e);
			}
			if (handler != null) {
				return handler.getPeripheral(te);// TODO doesn't work?
			}
		}
		return null;
	}
	/**
	 * Checks whether a given tile entity is a computer tile entity.
	 * 
	 * In general, this is
	 * <code>te instanceof dan200.computer.shared.IComputerEntity</code>.
	 * 
	 * @param te
	 *            the tile entity
	 * @return true if the tile is a computer tile (or a turtle)
	 */
	public static boolean isComputer(TileEntity te) {
		if (ComputerCraftReflector.class_IComputerEntity != null) {
			return ComputerCraftReflector.class_IComputerEntity.isInstance(te);
		}
		return false;// computercraft is apparently not installed.
	}

	/**
	 * Initialize the reflection data
	 */
	protected static void initReflectionReferences() {
		try {
			class_IComputerEntity = Class
					.forName("dan200.computer.shared.IComputerEntity");
	
			Class<?> ccc = Class.forName("dan200.ComputerCraft");
			method_getPeripheralFromClass = ccc.getMethod(
					"getPeripheralFromClass", Class.class);
			if (!method_getPeripheralFromClass.isAccessible()) {
				method_getPeripheralFromClass.setAccessible(true);
			}
		} catch (ClassNotFoundException e1) {
			PeripheralCableMod.MOD_LOGGER
					.log(Level.SEVERE,
							"Could not find the ComputerCraft mod in your minecraft.\n "
									+ "Although this mod would work without it, this wouldn't make much sense.");
		} catch (NoSuchMethodException e) {
			PeripheralCableMod.MOD_LOGGER
					.log(Level.SEVERE,
							"Could not find the ComputerCraft external peripheral query method.\n "
									+ "This means that you can't use external peripherals (eg. command block) over cables.");
		}
	}

}
