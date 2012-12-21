package xfel.mods.debug;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.LanguageRegistry;

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
