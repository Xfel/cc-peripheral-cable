package xfel.mods.cccable.common;

import xfel.mods.cccable.common.blocks.BlockPeripheralCable;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Block;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "CCCable", version = "@mod.version@", useMetadata = false, name = "ComputerCraft Peripheral Cables")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class PeripheralCableMod {
	
	@SidedProxy(clientSide="xfel.mods.cccable.client.ClientProxy",serverSide="xfel.mods.cccable.common.CommonProxy")
	public static CommonProxy sideHandler;
	
	@Instance
	public static PeripheralCableMod instance;
	
	@Block(name = "peripheralcable")
	public static BlockPeripheralCable cableBlock;
	
	@Init
	public void init(FMLInitializationEvent evt){
		
		sideHandler.initSide();
		
	}
}
