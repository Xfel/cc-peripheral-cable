/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */package xfel.mods.cccable.client;

import net.minecraftforge.client.MinecraftForgeClient;
import xfel.mods.cccable.common.CommonProxy;
import xfel.mods.cccable.common.PeripheralCableMod;
import cpw.mods.fml.client.registry.RenderingRegistry;

/**
 * Client-Side proxy class. Manages the rendering code.
 * 
 * @author Xfel
 *
 */
public class ClientProxy extends CommonProxy {
	
	@Override
	protected void initSide() {
		MinecraftForgeClient.preloadTexture(BLOCK_TEXTURE);
		
		int cableRenderId = RenderingRegistry.getNextAvailableRenderId();
		PeripheralCableMod.cableBlock.setRenderType(cableRenderId);
		RenderingRegistry.registerBlockHandler(cableRenderId, new ExtendedCableRenderer());
	}
	
}
