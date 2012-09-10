package xfel.mods.debug;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Material;
import net.minecraft.src.Packet3Chat;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class BlockDebugPeripheral extends BlockContainer {

	public static class TileDebugPeripheral extends TileEntity implements IPeripheral{

		@Override
		public String getType() {
			return "debugperipheral";
		}

		@Override
		public String[] getMethodNames() {
			return new String[]{"test"};
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, int method,
				Object[] arguments) throws Exception {
			StringBuilder sb=new StringBuilder("Method called with arguments:");
			
			for (int i = 0; i < arguments.length; i++) {
				sb.append(' ');
				sb.append(arguments[i]);
			}
			
		    MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(new Packet3Chat(sb.toString()));
	        return null;
		}

		@Override
		public boolean canAttachToSide(int side) {
			return true;
		}

		@Override
		public void attach(IComputerAccess computer, String computerSide) {
			System.out.printf("Computer %d attached on side %s%n",computer.getID(),computerSide);
		}

		@Override
		public void detach(IComputerAccess computer) {
			System.out.printf("Computer %d detached%n",computer.getID());
		}

	}

	public BlockDebugPeripheral(int id) {
		super(id, Material.iron);
		setBlockName("debug.peripheral");
		setCreativeTab(CreativeTabs.tabMisc);
		
		GameRegistry.registerBlock(this);
		GameRegistry.registerTileEntity(TileDebugPeripheral.class, "DebugPeripheral");
	}
	
	@Override
	public int getBlockTextureFromSide(int side) {
		return side;
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileDebugPeripheral();
	}

}
