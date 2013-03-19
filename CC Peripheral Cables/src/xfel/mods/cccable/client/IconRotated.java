package xfel.mods.cccable.client;

import net.minecraft.util.Icon;

/**
 * A wrapper to rotate an {@link Icon}
 * 
 * @author Xfel
 * 
 */
public class IconRotated implements Icon {

	private Icon target;

	private int rotation;

	/**
	 * @param target
	 * @param rotation
	 */
	public IconRotated(Icon target, int rotation) {
		this.target = target;
		this.rotation = 4-rotation;
	}
	
	private float getTexCoord(int side){
		switch((rotation+side)%4){
		case 0:
			// left
			return target.func_94209_e();
		case 1:
			// bottom
			return target.func_94210_h();
		case 2:
			// right
			return target.func_94212_f();
		case 3:
			// top
			return target.func_94206_g();
		}
		
		return 0;// can't happen
	}

	public int func_94211_a() {
		return this.target.func_94211_a();
	}

	public int func_94216_b() {
		return this.target.func_94216_b();
	}

	public float func_94209_e() {
		// left
		return getTexCoord(0);
	}

	public float func_94212_f() {
		// right
		return getTexCoord(2);
	}

	public float func_94214_a(double par1) {
		float f = this.func_94212_f() - this.func_94209_e();
		return this.func_94209_e() + f * ((float) par1 / 16.0F);
	}

	public float func_94206_g() {
		// top
		return getTexCoord(3);
	}

	public float func_94210_h() {
		// bottom
		return getTexCoord(1);
	}

	public float func_94207_b(double par1) {
		float f = this.func_94210_h() - this.func_94206_g();
		return this.func_94206_g() + f * ((float) par1 / 16.0F);
	}

	public String func_94215_i() {
		return this.target.func_94215_i();
	}

	public int func_94213_j() {
		return this.target.func_94213_j();
	}

	public int func_94208_k() {
		return this.target.func_94208_k();
	}

}
