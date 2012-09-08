/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */package xfel.mods.cccable.common.blocks;

import net.minecraft.src.BlockContainer;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class BlockPeripheralCable extends BlockContainer {

	private int renderType = -1;

	protected BlockPeripheralCable(int id) {
		super(id, Material.glass);
		setBlockName("cable.peripheral");
		setTextureFile("terrain/ccable.png");
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		if (world.isRemote) {
			return new TilePeripheralCableCommon();
		}
		return new TilePeripheralCableServer();
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

		if (te instanceof TilePeripheralCableServer) {
			TilePeripheralCableServer tpc = (TilePeripheralCableServer) te;
			tpc.connectionStateDirty = true;
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int blockId, int blockmeta) {
		TileEntity te = world.getBlockTileEntity(x, y, z);

		if (te instanceof TilePeripheralCableServer) {
			TilePeripheralCableServer tpc = (TilePeripheralCableServer) te;
			tpc.cleanup();
		}
		super.breakBlock(world, x, y, z, blockId, blockmeta);
	}
}
