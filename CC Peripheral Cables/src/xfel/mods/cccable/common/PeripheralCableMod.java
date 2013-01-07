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

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
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
import dan200.computer.api.ComputerCraftAPI;

/**
 * Main mod class
 * 
 * @author Xfel
 * 
 */
@Mod(modid = "CCCable", useMetadata = false, name = "ComputerCraft Peripheral Cables", dependencies = "after:ComputerCraft")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class PeripheralCableMod {

	/**
	 * Global logger for the mod.
	 */
	public static final Logger MOD_LOGGER;

	static {
		MOD_LOGGER = Logger.getLogger("CCCable");
		MOD_LOGGER.setParent(FMLLog.getLogger());
	}

	/**
	 * The sided proxy
	 */
	@SidedProxy(clientSide = "xfel.mods.cccable.client.ClientProxy", serverSide = "xfel.mods.cccable.common.CommonProxy")
	public static CommonProxy sideHandler;

	/**
	 * the mod instance
	 */
	@Instance
	public static PeripheralCableMod instance;

	/**
	 * The mod metadata
	 */
	@Metadata
	public static ModMetadata metadata;

	/**
	 * The peripheral cable block instance
	 */
	public static BlockCable cableBlock;

	private File minecraftDirectory;

	/**
	 * Retrieves the minecraft directory (as there seems to be no other way)
	 * Also loads the block id from config.
	 * 
	 * @param evt
	 *            state event
	 */
	@PreInit
	public void loadConfig(FMLPreInitializationEvent evt) {
		System.out.println(sideHandler);
		minecraftDirectory = evt.getModConfigurationDirectory().getParentFile();

		Configuration config = new Configuration(
				evt.getSuggestedConfigurationFile());
		config.load();

		cableBlock = new BlockCable(config.getBlock("cable.id", 2030).getInt());
		GameRegistry.registerBlock(cableBlock, "PeripheralCable");

		config.save();
	}

	/**
	 * Registers blocks, names, tile entities and recipes
	 * 
	 * @param evt
	 *            state event
	 */
	@Init
	public void init(FMLInitializationEvent evt) {

		LanguageRegistry.addName(cableBlock, "Peripheral Cable");
		CraftingManager.getInstance().func_92051_a(
				new ItemStack(cableBlock, 6), "SRS", "RIR", "SRS", 'R',
				Item.redstone, 'S', Block.stone, 'I', Item.ingotIron);

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
	 * 
	 * @param evt
	 *            state event
	 */
	@PostInit
	public void postInit(FMLPostInitializationEvent evt) {
		// set the creative tab...
		if (ComputerCraftAPI.getCreativeTab() != null) {
			cableBlock.setCreativeTab(ComputerCraftAPI.getCreativeTab());
		}

		// load reflection data
		ComputerCraftReflector.initReflectionReferences();

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

				MOD_LOGGER.log(Level.INFO,
						"Existing peripheral api file found, has version "
								+ version);
				if (version.compareTo(metadata.version) >= 0) {
					return;
				}
			}
		} else {
			MOD_LOGGER.log(Level.INFO,
					"Peripheral api file doesn't exist; creating");
			if (!apiLoc.getParentFile().exists()) {
				apiLoc.getParentFile().mkdirs();
			}
		}

		MOD_LOGGER.log(Level.INFO,
				"Injecting peripheral api file with version "
						+ metadata.version);
		InputStream fileSource = getClass().getResourceAsStream(
				"/lua/peripheralAPI.lua");
		OutputStream fos = null;
		try {
			fos = new FileOutputStream(apiLoc);

			fos.write("-- v".getBytes());
			fos.write(metadata.version.getBytes());
			fos.write('\n');

			byte[] b = new byte[4096];
			int read;
			while ((read = fileSource.read(b)) != -1) {
				fos.write(b, 0, read);
			}
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
