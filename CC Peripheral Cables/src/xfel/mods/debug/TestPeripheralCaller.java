package xfel.mods.debug;

import java.util.Arrays;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.LanguageRegistry;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;

public class TestPeripheralCaller extends Item {

	private static final String METHOD = "call";
	private static final Object[] ARGS = { "black", "test", "Hello",
			Integer.valueOf(1) };

	public TestPeripheralCaller(int id) {
		super(id);
		setItemName("debug.caller");
		setCreativeTab(CreativeTabs.tabMisc);

		LanguageRegistry.addName(this, "Test peripheral method invoker");
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player,
			World world, int x, int y, int z, int side, float hitX, float hitY,
			float hitZ) {
		TileEntity te = world.getBlockTileEntity(x, y, z);

		if (te instanceof IPeripheral) {
			IPeripheral peri = (IPeripheral) te;

			String[] methods = peri.getMethodNames();
			int mi = -1;
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].equals(METHOD)) {
					mi = i;
					break;
				}
			}
			if (mi != -1) {
				IComputerAccess dummyComputer = new DummyComputer();
				peri.attach(dummyComputer);
				try {
					System.out.println(Arrays.toString(peri.callMethod(
							dummyComputer, mi, ARGS)));
				} catch (Exception e) {
					System.out.println("Error executing method");
					e.printStackTrace(System.out);
				} finally {
					peri.detach(dummyComputer);
				}
			}
			return true;
		}

		return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX,
				hitY, hitZ);
	}

}
