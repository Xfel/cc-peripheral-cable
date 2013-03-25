package xfel.mods.cccable.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.Icon;

/**
 * A wrapper to rotate an {@link Icon}
 * 
 * @author Xfel
 * 
 */
// FIXME wenn die textur nicht um 0° oder 180° gedreht wird, landen die
// texturkoordinaten an der gegenüberliegenden seite der gesamttextur. Was tun?

@SideOnly(Side.CLIENT)
public class IconRotated implements Icon {

	private Icon target;

	private int rotation;

	/**
	 * @param target
	 * @param rotation
	 */
	public IconRotated(Icon target, int rotation) {
		this.target = target;
		this.rotation = rotation;
	}

	private float getTexCoord(int side) {
		switch ((rotation + side) % 4) {
		case 0:
			// left
			return target.getMinU();
		case 1:
			// bottom
			return target.getMaxV();
		case 2:
			// right
			return target.getMaxU();
		case 3:
			// top
			return target.getMinV();
		}

		return 0;// can't happen
	}

	@Override
	public int getOriginX() {
		return this.target.getOriginX();
	}

	@Override
	public int getOriginY() {
		return this.target.getOriginY();
	}

	@Override
	public float getMinU() {
		// left
		return getTexCoord(0);
	}

	@Override
	public float getMaxU() {
		// right
		return getTexCoord(2);
	}

	@Override
	public float getInterpolatedU(double par1) {
		float f = this.getMaxU() - this.getMinU();
		return this.getMinU() + f * ((float) par1 / 16.0F);
	}

	@Override
	public float getMinV() {
		// top
		return getTexCoord(3);
	}

	@Override
	public float getMaxV() {
		// bottom
		return getTexCoord(1);
	}

	@Override
	public float getInterpolatedV(double par1) {
		float f = this.getMaxV() - this.getMinV();
		return this.getMinV() + f * ((float) par1 / 16.0F);
	}

	@Override
	public String getIconName() {
		return this.target.getIconName();
	}

	@Override
	public int getSheetWidth() {
		return this.target.getSheetWidth();
	}

	@Override
	public int getSheetHeight() {
		return this.target.getSheetHeight();
	}

}
