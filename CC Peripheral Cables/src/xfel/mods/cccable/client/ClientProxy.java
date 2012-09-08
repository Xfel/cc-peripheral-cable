/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */package xfel.mods.cccable.client;

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
