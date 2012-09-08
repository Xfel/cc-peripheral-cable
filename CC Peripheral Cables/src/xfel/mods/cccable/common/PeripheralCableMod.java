/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */package xfel.mods.cccable.common;

import xfel.mods.cccable.common.blocks.BlockCable;
import xfel.mods.cccable.common.blocks.TileCableServer;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Block;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "CCCable", version = "@mod.version@", useMetadata = false, name = "ComputerCraft Peripheral Cables")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class PeripheralCableMod {
	
	@SidedProxy(clientSide="xfel.mods.cccable.client.ClientProxy",serverSide="xfel.mods.cccable.common.CommonProxy")
	public static CommonProxy sideHandler;
	
	@Instance
	public static PeripheralCableMod instance;
	
	@Block(name = "peripheralcable")
	public static BlockCable cableBlock;
	
	@Init
	public void init(FMLInitializationEvent evt){
		
		sideHandler.initSide();
		
		GameRegistry.registerTileEntity(TileCableServer.class, "PeripheralCable");
		
	}
}
