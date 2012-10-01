package xfel.mods.debug;

import java.util.Arrays;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class ItemDumper extends Item{

	public ItemDumper(int id) {
		super(id);
		setItemName("debug.dumper");
		setCreativeTab(CreativeTabs.tabMisc);
		
		LanguageRegistry.addName(this, "Tile Entity Dumper");
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player,
			World world, int x, int y, int z, int side, float hitX, float hitY,
			float hitZ) {
		if(world.isRemote)return false;
		
		TileEntity te=world.getBlockTileEntity(x, y, z);
		if(te==null)return true;
		
		player.addChatMessage(String.valueOf(te));
		
		return true;
	}

}
