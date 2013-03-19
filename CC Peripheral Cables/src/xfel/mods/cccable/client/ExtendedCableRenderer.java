package xfel.mods.cccable.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import xfel.mods.cccable.common.PeripheralCableMod;
import xfel.mods.cccable.common.blocks.BlockCable;
import xfel.mods.cccable.common.blocks.TileCableCommon;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

/**
 * An extend renderer to make cables look nicer
 * 
 * @author Xfel
 * 
 */
public class ExtendedCableRenderer implements ISimpleBlockRenderingHandler {

	/**
	 * All directions orthogonal to the given directions
	 */
	public static final int[][] ORTHOGONAL_DIRECTIONS = { { 2, 4, 3, 5 },
			{ 2, 4, 3, 5 }, { 1, 4, 0, 5 }, { 1, 4, 0, 5 }, { 1, 2, 0, 3 },
			{ 1, 2, 0, 3 } };

	/**
	 * the flags for the orhogonal directions. cached for performance gain
	 */
	public static final int[][] OFLAGS = new int[6][4];
	/**
	 * The orthogonal direction masks
	 */
	public static final int[] OMASKS = new int[6];

	static {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 4; j++) {
				int flag = ForgeDirection.VALID_DIRECTIONS[ORTHOGONAL_DIRECTIONS[i][j]].flag;
				OFLAGS[i][j] = flag;
				OMASKS[i] |= flag;
			}
		}
	}

	/**
	 * Computes the texture index for the given side and connections
	 * 
	 * @param connectionState
	 *            the block's connection state
	 * @param side
	 *            the block side to render
	 * @param outRotate
	 *            will contain the uv rotation value
	 * @return the texture index
	 */
	public static Icon selectTexture(int connectionState, int side,
			int[] outRotate
			) {

		connectionState &= OMASKS[side];

		if (connectionState == 0 || connectionState == OMASKS[side]) {
			outRotate[0] = 0;
			return cableBase;// all directions or no connection
		}

		int[] orths = OFLAGS[side];

		for (int i = 0; i < 2; i++) {
			if (connectionState == (orths[i] | orths[i + 2])
					|| connectionState == orths[i]
					|| connectionState == orths[i + 2]) {
				// straight
				outRotate[0] = i;
				return new IconRotated(cableStraight, i);
//				return 16 + i;
			}
		}

		for (int i = 0; i < 4; i++) {
			if (connectionState == (orths[i] | orths[(i + 1) % 4])) {
				// corner
				outRotate[0] = i;
				return new IconRotated(cableCorner, i);
//				return 32 + i;
			}
		}

		for (int i = 0; i < 4; i++) {
			if (connectionState == (orths[i] | orths[(i + 1) % 4] | orths[(i + 2) % 4])) {
				// T
				outRotate[0] = i;
				return new IconRotated(cableT, i);
//				return 48 + i;
			}
		}

		return cableBase;
	}

	/**
	 * Renders a cable with the given connection state and color tag at the
	 * given position.
	 * 
	 * @param renderblocks
	 * @param world
	 * @param block
	 * @param connectionState
	 * @param colorTag
	 * @param x
	 * @param y
	 * @param z
	 */
	private static void renderCable(RenderBlocks renderblocks,
			IBlockAccess world, Block block, int connectionState, int colorTag,
			int x, int y, int z) {
		final float minSize = 0.25f;
		final float maxSize = 0.75f;

		float xmin = (connectionState & ForgeDirection.WEST.flag) != 0 ? 0.0f
				: minSize;
		float ymin = (connectionState & ForgeDirection.DOWN.flag) != 0 ? 0.0f
				: minSize;
		float zmin = (connectionState & ForgeDirection.NORTH.flag) != 0 ? 0.0f
				: minSize;

		float xmax = (connectionState & ForgeDirection.EAST.flag) != 0 ? 1.0f
				: maxSize;
		float ymax = (connectionState & ForgeDirection.UP.flag) != 0 ? 1.0f
				: maxSize;
		float zmax = (connectionState & ForgeDirection.SOUTH.flag) != 0 ? 1.0f
				: maxSize;

		// This code is basically an adapted version of
		// RenderBlocks.renderStandardBlockWithColorMultiplier
		float colorR = 1.0f;
		float colorG = 1.0f;
		float colorB = 1.0f;

		if (EntityRenderer.anaglyphEnable) {
			float colorRtemp = (colorR * 30.0F + colorG * 59.0F + colorB * 11.0F) / 100.0F;
			float colorGtemp = (colorR * 30.0F + colorG * 70.0F) / 100.0F;
			float colorBtemp = (colorR * 30.0F + colorB * 70.0F) / 100.0F;
			colorR = colorRtemp;
			colorG = colorGtemp;
			colorB = colorBtemp;
		}

		Tessellator tess = Tessellator.instance;

		float colorTopFactor = 1.0F;
		float colorBottomFactor = 0.5F;
		float colorNSFactor = 0.8F;
		float colorOWFactor = 0.6F;

		float colorTopR = colorTopFactor * colorR;
		float colorTopG = colorTopFactor * colorG;
		float colorTopB = colorTopFactor * colorB;
		float colorBottomR = colorBottomFactor * colorR;
		float colorBottomG = colorBottomFactor * colorG;
		float colorBottomB = colorBottomFactor * colorB;
		float colorNSR = colorNSFactor * colorR;
		float colorNSG = colorNSFactor * colorG;
		float colorNSB = colorNSFactor * colorB;
		float colorOWR = colorOWFactor * colorR;
		float colorOWG = colorOWFactor * colorG;
		float colorOWB = colorOWFactor * colorB;
		
		int[] uvRotatePointer=new int[1];

		int brightness = block.getMixedBrightnessForBlock(world, x, y, z);
		tess.setBrightness(brightness);

		// render Y +- faces
		renderblocks.setRenderBounds(xmin, minSize, zmin, xmax, maxSize, zmax);

		tess.setColorOpaque_F(colorBottomR, colorBottomG, colorBottomB);
		renderblocks.renderBottomFace(block, (double) x, (double) y,
				(double) z, selectTexture(connectionState, 0, uvRotatePointer));

		tess.setColorOpaque_F(colorTopR, colorTopG, colorTopB);
		renderblocks.renderTopFace(block, (double) x, (double) y, (double) z,
				selectTexture(connectionState, 1, uvRotatePointer));

		// render Z +- faces
		renderblocks.setRenderBounds(xmin, ymin, minSize, xmax, ymax, maxSize);

		tess.setColorOpaque_F(colorNSR, colorNSG, colorNSB);
		renderblocks.flipTexture = true;// flip the texture to avoid render bug
		renderblocks.renderEastFace(block, (double) x, (double) y, (double) z,
				selectTexture(connectionState, 2, uvRotatePointer));
		renderblocks.flipTexture = false;

		renderblocks.renderWestFace(block, (double) x, (double) y, (double) z,
				selectTexture(connectionState, 3, uvRotatePointer));

		// render X +- faces
		renderblocks.setRenderBounds(minSize, ymin, zmin, maxSize, ymax, zmax);

		tess.setColorOpaque_F(colorOWR, colorOWG, colorOWB);

		renderblocks.renderNorthFace(block, (double) x, (double) y, (double) z,
				selectTexture(connectionState, 4, uvRotatePointer));

		renderblocks.flipTexture = true;// flip the texture to avoid render bug
		renderblocks.renderSouthFace(block, (double) x, (double) y, (double) z,
				selectTexture(connectionState, 5, uvRotatePointer));
		renderblocks.flipTexture = false;

		// render color tag if there is one
		if (colorTag != -1) {
			final float colorOffset = 0.001f;// the color field is rendered a
												// little bit above the cable
												// surface.

			renderblocks.setRenderBounds(minSize - colorOffset, minSize
					- colorOffset, minSize - colorOffset,
					maxSize + colorOffset, maxSize + colorOffset, maxSize
							+ colorOffset);
			// renderblocks.overrideBlockTexture = 64 + colorTag;
			renderblocks.setOverrideBlockTexture(getColorIcon(colorTag));

			renderblocks.renderStandardBlockWithColorMultiplier(block, x, y, z,
					colorR, colorG, colorB);

			renderblocks.clearOverrideBlockTexture();
		}
		renderblocks.unlockBlockBounds();

	}

	private static Icon getColorIcon(int colorTag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;

		Icon textureID = cableBase;

		renderer.setRenderBounds(0.25f, 0.0F, 0.25f, 0.75f, 1.0F, 0.75f);

		GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		renderer.renderBottomFace(block, 0.0D, 0.0D, 0.0D, textureID);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderTopFace(block, 0.0D, 0.0D, 0.0D, textureID);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		renderer.renderEastFace(block, 0.0D, 0.0D, 0.0D, textureID);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderWestFace(block, 0.0D, 0.0D, 0.0D, textureID);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		renderer.renderNorthFace(block, 0.0D, 0.0D, 0.0D, textureID);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderSouthFace(block, 0.0D, 0.0D, 0.0D, textureID);
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		renderer.unlockBlockBounds();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);

		if (tile instanceof TileCableCommon) {
			TileCableCommon pipeTile = (TileCableCommon) tile;
			renderCable(renderer, world, block, pipeTile.getConnectionState(),
					pipeTile.getColorTag(), x, y, z);
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

	private static Icon cableBase, cableStraight, cableCorner, cableT;

	public static void loadTextures(IconRegister iconRegister) {
		PeripheralCableMod.MOD_LOGGER.info("Loading textures...");
		cableBase = iconRegister.func_94245_a("cccable:cable-base");
		cableStraight = iconRegister.func_94245_a("cccable:cable-straight");
		cableCorner = iconRegister.func_94245_a("cccable:cable-corner");
		cableT = iconRegister.func_94245_a("cccable:cable-t");
		System.out.println(cableBase);
		// cableBase=iconRegister.func_94245_a("cccable:cable-base");
	}
}
