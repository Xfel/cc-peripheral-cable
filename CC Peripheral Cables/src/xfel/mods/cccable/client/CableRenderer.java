package xfel.mods.cccable.client;

import xfel.mods.cccable.common.blocks.BlockPeripheralCable;
import xfel.mods.cccable.common.blocks.TilePeripheralCableCommon;
import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class CableRenderer implements ISimpleBlockRenderingHandler {

	private static void renderCable(RenderBlocks renderblocks,
			IBlockAccess world, Block block, int connectionState, int x, int y,
			int z) {
		float minSize = 0.25f;
		float maxSize = 0.75f;

		// state.currentTextureIndex =
		// state.textureMatrix.getTextureIndex(ForgeDirection.UNKNOWN);
		block.setBlockBounds(minSize, minSize, minSize, maxSize, maxSize,
				maxSize);
		renderblocks.renderStandardBlock(block, x, y, z);

		if (BlockPeripheralCable.isConnected(connectionState,
				ForgeDirection.WEST)) {
			// state.currentTextureIndex =
			// state.textureMatrix.getTextureIndex(ForgeDirection.WEST);
			block.setBlockBounds(0.0F, minSize, minSize, minSize, maxSize,
					maxSize);
			renderblocks.renderStandardBlock(block, x, y, z);
		}

		if (BlockPeripheralCable.isConnected(connectionState,
				ForgeDirection.EAST)) {
			// state.currentTextureIndex =
			// state.textureMatrix.getTextureIndex(ForgeDirection.EAST);
			block.setBlockBounds(maxSize, minSize, minSize, 1.0F, maxSize,
					maxSize);
			renderblocks.renderStandardBlock(block, x, y, z);
		}

		if (BlockPeripheralCable.isConnected(connectionState,
				ForgeDirection.DOWN)) {
			// state.currentTextureIndex =
			// state.textureMatrix.getTextureIndex(ForgeDirection.DOWN);
			block.setBlockBounds(minSize, 0.0F, minSize, maxSize, minSize,
					maxSize);
			renderblocks.renderStandardBlock(block, x, y, z);
		}

		if (BlockPeripheralCable
				.isConnected(connectionState, ForgeDirection.UP)) {
			// state.currentTextureIndex =
			// state.textureMatrix.getTextureIndex(ForgeDirection.UP);
			block.setBlockBounds(minSize, maxSize, minSize, maxSize, 1.0F,
					maxSize);
			renderblocks.renderStandardBlock(block, x, y, z);
		}

		if (BlockPeripheralCable.isConnected(connectionState,
				ForgeDirection.NORTH)) {
			// state.currentTextureIndex =
			// state.textureMatrix.getTextureIndex(ForgeDirection.NORTH);
			block.setBlockBounds(minSize, minSize, 0.0F, maxSize, maxSize,
					minSize);
			renderblocks.renderStandardBlock(block, x, y, z);
		}

		if (BlockPeripheralCable.isConnected(connectionState,
				ForgeDirection.SOUTH)) {
			// state.currentTextureIndex =
			// state.textureMatrix.getTextureIndex(ForgeDirection.SOUTH);
			block.setBlockBounds(minSize, minSize, maxSize, maxSize, maxSize,
					1.0F);
			renderblocks.renderStandardBlock(block, x, y, z);
		}
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);

		if (tile instanceof TilePeripheralCableCommon) {
			TilePeripheralCableCommon pipeTile = (TilePeripheralCableCommon) tile;
			renderCable(renderer, world, block, pipeTile.getConnectionState(),
					x, y, z);
		}
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return 0;
	}

}
