/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */package xfel.mods.cccable.common.blocks;

import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockCable extends BlockContainer {

	private int renderType = -1;

	public BlockCable(int id) {
		super(id, Material.glass);
		setBlockName("cable.peripheral");
		setTextureFile("/terrain/cccable.png");
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		if (world.isRemote) {
			return new TileCableCommon();
		}
		return new TileCableServer();
	}

	@Override
	public int getRenderType() {
		return renderType;
	}

	public void setRenderType(int renderType) {
		this.renderType = renderType;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z,
			int blockId) {
		TileEntity te = world.getBlockTileEntity(x, y, z);

		if (te instanceof TileCableServer) {
			TileCableServer tpc = (TileCableServer) te;
			tpc.connectionStateDirty = true;
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int blockId, int blockmeta) {
		TileEntity te = world.getBlockTileEntity(x, y, z);

		if (te instanceof TileCableServer) {
			TileCableServer tpc = (TileCableServer) te;
			tpc.cleanup();
		}
		super.breakBlock(world, x, y, z, blockId, blockmeta);
	}
	
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int direction, float offsetX, float offsetY, float offsetZ) {
		TileEntity te = world.getBlockTileEntity(x, y, z);

		if (te instanceof TileCableCommon) {
			TileCableCommon tpc = (TileCableCommon) te;
			
			ItemStack iih=player.getCurrentEquippedItem();
			
			if(iih.getItem()==Item.dyePowder){
				tpc.setColorTag(iih.getItemDamage());
				if (!player.capabilities.isCreativeMode) {
					iih.stackSize--;
					if (iih.stackSize == 0) {
						player.destroyCurrentEquippedItem();
					}
				}
				return true;
			}
			
			if(iih.getItem()==Item.bucketWater){
				tpc.setColorTag(-1);
				return true;
			}
		}
		return false;
	}

	public static boolean isConnected(int connectionState, ForgeDirection dir) {
		return  (connectionState&dir.flag)!=0;
	}
	
	public int getBlockTexture(IBlockAccess iba, int x, int y, int z, int side) {
		TileEntity te = iba.getBlockTileEntity(x, y, z);

		if (te instanceof TileCableCommon) {
			TileCableCommon tpc = (TileCableCommon) te;
			
			return tpc.getColorTag()+1;
		}
		return 0;
	}
}
