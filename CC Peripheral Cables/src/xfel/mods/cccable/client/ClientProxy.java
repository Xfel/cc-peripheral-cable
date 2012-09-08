package xfel.mods.cccable.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import xfel.mods.cccable.common.CommonProxy;
import xfel.mods.cccable.common.PeripheralCableMod;

public class ClientProxy extends CommonProxy {
	
	@Override
	protected void initSide() {
		PeripheralCableMod.cableBlock.setRenderType(RenderingRegistry.getNextAvailableRenderId());
	}
	
}
