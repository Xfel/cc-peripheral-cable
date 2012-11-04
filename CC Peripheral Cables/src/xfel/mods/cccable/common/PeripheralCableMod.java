/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.cccable.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.src.CraftingManager;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Configuration;
import xfel.mods.cccable.common.blocks.BlockCable;
import xfel.mods.cccable.common.blocks.TileCableServer;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
//import xfel.mods.debug.BlockDebugPeripheral;
//import xfel.mods.debug.ItemDumper;
//import xfel.mods.debug.TestPeripheralCaller;

/**
 * Main mod class
 * 
 * @author Xfel
 * 
 */
@Mod(modid = "CCCable", version = PeripheralCableMod.MOD_VERSION, useMetadata = false, name = "ComputerCraft Peripheral Cables")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class PeripheralCableMod {

	public static final Logger MOD_LOGGER;

	static {
		MOD_LOGGER = Logger.getLogger("CCCable");
		MOD_LOGGER.setParent(FMLLog.getLogger());
	}

	static final String MOD_VERSION = "@mod.version@";

	@SidedProxy(clientSide = "xfel.mods.cccable.client.ClientProxy", serverSide = "xfel.mods.cccable.common.CommonProxy")
	public static CommonProxy sideHandler;

	@Instance
	public static PeripheralCableMod instance;

	@Metadata
	public static ModMetadata metadata;

	// @Block(name = "PeripheralCable")
	public static BlockCable cableBlock;

	private File minecraftDirectory;

	/**
	 * Retrieves the minecraft directory (as there seems to be no other way)
	 */
	@PreInit
	public void loadConfig(FMLPreInitializationEvent evt) {
		minecraftDirectory = evt.getModConfigurationDirectory().getParentFile();
		evt.getModMetadata().version = MOD_VERSION;

		// compability until @Block is working
		// if (cableBlock == null) {
		Configuration config = new Configuration(
				evt.getSuggestedConfigurationFile());
		config.load();

		cableBlock = new BlockCable(config.getBlock(
				"cable.id", 2030).getInt());
		GameRegistry.registerBlock(cableBlock);
		// }

		config.save();
	}

	/**
	 * Registers blocks, names, tile entities and recipes
	 */
	@Init
	public void init(FMLInitializationEvent evt) {

		LanguageRegistry.addName(cableBlock, "Peripheral Cable");
		CraftingManager.getInstance().addRecipe(new ItemStack(cableBlock, 6),
				"SRS", "RIR", "SRS", 'R', Item.redstone, 'S',
				net.minecraft.src.Block.stone, 'I', Item.ingotIron);

		sideHandler.initSide();

		GameRegistry.registerTileEntity(TileCableServer.class,
				"PeripheralCable");

		// debug code:
		// new BlockDebugPeripheral(3333);
		// new TestPeripheralCaller(3334);
		// new ItemDumper(3335);
	}

	/**
	 * Injects the new peripheral api file
	 */
	@PostInit
	public void postInit(FMLPostInitializationEvent evt) {
		// inject the file into rom

		File apiLoc = new File(minecraftDirectory,
				"mods/ComputerCraft/lua/rom/apis/peripheral");
		if (apiLoc.exists()) {

			BufferedReader br = null;
			String firstline = "";
			try {
				br = new BufferedReader(new FileReader(apiLoc));

				firstline = br.readLine();

			} catch (IOException e) {
				MOD_LOGGER.log(Level.SEVERE, "Error reading version tag", e);
				} finally {

				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						MOD_LOGGER.log(Level.SEVERE, "Error closing file", e);
					}
				}
			}

			int vmIndex = firstline.indexOf('v');

			if (vmIndex != -1) {
				String version = firstline.substring(vmIndex + 1);

				MOD_LOGGER.log(Level.INFO, "Existing peripheral api file found, has version "+version);
				if (version.compareTo(MOD_VERSION) >= 0) {
					return;
				}
			}
		} else {
			MOD_LOGGER.log(Level.INFO, "Peripheral api file doesn't exist; creating");
			if (!apiLoc.getParentFile().exists()) {
				apiLoc.getParentFile().mkdirs();
			}
		}

		MOD_LOGGER.log(Level.INFO, "Injecting peripheral api file with version "+MOD_VERSION);
		InputStream fileSource = getClass().getResourceAsStream(
				"/lua/peripheralAPI.lua");
		OutputStream fos = null;
		try {
			fos = new FileOutputStream(apiLoc);

			fos.write("-- v".getBytes());
			fos.write(MOD_VERSION.getBytes());
			fos.write('\n');

			byte[] b = new byte[4096];
			int read;
			while ((read = fileSource.read(b)) != -1)
				fos.write(b, 0, read);
		} catch (IOException e) {
			MOD_LOGGER.log(Level.SEVERE, "Error copying file", e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					MOD_LOGGER.log(Level.SEVERE, "Error closing file", e);
				}
			}
		}
	}
}
