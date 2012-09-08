package xfel.mods.cccable.common.blocks;

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
	public TileEntity createNewTileEntity(World var1) {
		// TODO Auto-generated method stub
		return null;
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
}
