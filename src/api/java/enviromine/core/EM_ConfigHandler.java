package enviromine.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;

import enviromine.trackers.properties.ArmorProperties;
import enviromine.trackers.properties.BiomeProperties;
import enviromine.trackers.properties.BlockProperties;
import enviromine.trackers.properties.CaveBaseProperties;
import enviromine.trackers.properties.CaveGenProperties;
import enviromine.trackers.properties.CaveSpawnProperties;
import enviromine.trackers.properties.DimensionProperties;
import enviromine.trackers.properties.EntityProperties;
import enviromine.trackers.properties.ItemProperties;
import enviromine.trackers.properties.RotProperties;
import enviromine.trackers.properties.StabilityType;
import enviromine.trackers.properties.helpers.PropertyBase;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class EM_ConfigHandler
{
	// Dirs for Custom Files
	public static String configPath = "config/enviromine/";
	public static String customPath = configPath + "CustomProperties/";
	
	static HashMap<String, PropertyBase> propTypes;
	
	public static List loadedConfigs = new ArrayList(); 
	
	/**
	 * Register all property types and their category names here. The rest is handled automatically.
	 */
	static
	{
		propTypes = new HashMap<String, PropertyBase>();
		
		propTypes.put(CaveGenProperties.base.categoryName(), CaveGenProperties.base);
		propTypes.put(CaveSpawnProperties.base.categoryName(), CaveSpawnProperties.base);
		propTypes.put(CaveBaseProperties.base.categoryName(), CaveBaseProperties.base);
		propTypes.put(BiomeProperties.base.categoryName(), BiomeProperties.base);
		propTypes.put(ArmorProperties.base.categoryName(), ArmorProperties.base);
		propTypes.put(BlockProperties.base.categoryName(), BlockProperties.base);
		propTypes.put(DimensionProperties.base.categoryName(), DimensionProperties.base);
		propTypes.put(EntityProperties.base.categoryName(), EntityProperties.base);
		propTypes.put(ItemProperties.base.categoryName(), ItemProperties.base);
		propTypes.put(RotProperties.base.categoryName(), RotProperties.base);
	}
	
	public static int initConfig()
	{
		// Check for Data Directory 
		CheckDir(new File(customPath));
		
		EnviroMine.logger.log(Level.INFO, "Loading configs...");
		
		// load defaults
		
		//These must be run before the block configs generate/load
		StabilityType.base.GenDefaults();
		StabilityType.base.customLoad();
		
		if(EM_Settings.genDefaults)
		{
			loadDefaultProperties();
		}
		
		// Now load Files from "Custom Objects"
		File[] customFiles = GetFileList(customPath);
		for(int i = 0; i < customFiles.length; i++)
		{
			LoadCustomObjects(customFiles[i]);
		}
		
		// Load Main Config File And this will go though changes
		File configFile = new File(configPath + "EnviroMine.cfg");
		
		Iterator<PropertyBase> iterator = propTypes.values().iterator();
		
		// Load non standard property files
		while(iterator.hasNext())
		{
			PropertyBase props = iterator.next();
			
			if(!props.useCustomConfigs())
			{
				props.customLoad();
			}
		}
		
		loadGeneralConfig(configFile);
		
		int Total = EM_Settings.armorProperties.size() + EM_Settings.blockProperties.size() + EM_Settings.livingProperties.size() + EM_Settings.itemProperties.size() + EM_Settings.biomeProperties.size() + EM_Settings.dimensionProperties.size() + EM_Settings.caveGenProperties.size() + EM_Settings.caveSpawnProperties.size();
		
		return Total;
	}
	
	public static void loadGeneralConfig(File file)
	{
		Configuration config;
		try
		{
			config = new Configuration(file, true);
		} catch(Exception e)
		{
			EnviroMine.logger.log(Level.WARN, "Failed to load main configuration file!", e);
			return;
		}
		
		config.load();
		
		//World Generation
		EM_Settings.shaftGen = config.get("World Generation", "Enable Village MineShafts", true, "Generates mineshafts in villages").getBoolean(true);
		EM_Settings.oldMineGen = config.get("World Generation", "Enable New Abandoned Mineshafts", true, "Generates massive abandoned mineshafts (size doesn't cause lag)").getBoolean(true);
		EM_Settings.gasGen = config.get("World Generation", "Generate Gases", true).getBoolean(true);
		//EM_Settings.disableCaves = config.get("World Generation", "Disable Cave Dimension", false).getBoolean(false); // Moved to CaveBaseProperties
		//EM_Settings.limitElevatorY = config.get("World Generation", "Limit Elevator Height", true).getBoolean(true); // Moved to CaveBaseProperties
		
		//General Settings
		EM_Settings.enablePhysics = config.get(Configuration.CATEGORY_GENERAL, "Enable Physics", true, "Turn physics On/Off").getBoolean(true);
		EM_Settings.enableLandslide = config.get(Configuration.CATEGORY_GENERAL, "Enable Physics Landslide", true).getBoolean(true);
		EM_Settings.enableSanity = config.get(Configuration.CATEGORY_GENERAL, "Allow Sanity", true).getBoolean(true);
		EM_Settings.enableHydrate = config.get(Configuration.CATEGORY_GENERAL, "Allow Hydration", true).getBoolean(true);
		EM_Settings.enableBodyTemp = config.get(Configuration.CATEGORY_GENERAL, "Allow Body Temperature", true).getBoolean(true);
		EM_Settings.enableAirQ = config.get(Configuration.CATEGORY_GENERAL, "Allow Air Quality", true, "True/False to turn Enviromine Trackers for Sanity, Air Quality, Hydration, and Body Temperature.").getBoolean(true);
		EM_Settings.trackNonPlayer = config.get(Configuration.CATEGORY_GENERAL, "Track NonPlayer entities", false, "Track enviromine properties on Non-player entities(mobs & animals)").getBoolean(false);
		EM_Settings.updateCheck = config.get(Configuration.CATEGORY_GENERAL, "Check For Updates", true).getBoolean(true);
		EM_Settings.villageAssist = config.get(Configuration.CATEGORY_GENERAL, "Enable villager assistance", true).getBoolean(true);
		EM_Settings.foodSpoiling = config.get(Configuration.CATEGORY_GENERAL, "Enable food spoiling", true).getBoolean(true);
		EM_Settings.foodRotTime = config.get(Configuration.CATEGORY_GENERAL, "Default spoil time (days)", 7).getInt(7);
		EM_Settings.torchesBurn = config.get(Configuration.CATEGORY_GENERAL, "Torches burn", true).getBoolean(true);
		EM_Settings.torchesGoOut = config.get(Configuration.CATEGORY_GENERAL, "Torches go out", true).getBoolean(true);
		EM_Settings.finiteWater = config.get(Configuration.CATEGORY_GENERAL, "Finite Water", false).getBoolean(false);
		EM_Settings.noNausea = config.get(Configuration.CATEGORY_GENERAL, "Blindness instead of Nausea", false).getBoolean(false);
		EM_Settings.keepStatus = config.get(Configuration.CATEGORY_GENERAL, "Keep statuses on death", false).getBoolean(false);
		EM_Settings.renderGear = config.get(Configuration.CATEGORY_GENERAL, "Render Gear", true ,"Render 3d gear worn on player. Must reload game to take effect").getBoolean(true);
		
		// Physics Settings
		String PhySetCat = "Physics";
		int minPhysInterval = 6;
		EM_Settings.spreadIce = config.get(PhySetCat, "Large Ice Cracking", false, "Setting Large Ice Cracking to true can cause Massive Lag").getBoolean(false);
		EM_Settings.updateCap = config.get(PhySetCat, "Consecutive Physics Update Cap", 128, "This will change maximum number of blocks that can be updated with physics at a time. - 1 = Unlimited").getInt(128);
		EM_Settings.physInterval = getConfigIntWithMinInt(config.get(PhySetCat, "Physics Interval", minPhysInterval, "The number of ticks between physics update passes (must be " + minPhysInterval + " or more)"), minPhysInterval);
		EM_Settings.stoneCracks = config.get(PhySetCat, "Stone Cracks Before Falling", true).getBoolean(true);
		EM_Settings.defaultStability = config.get(PhySetCat, "Default Stability Type (BlockIDs > 175)", "loose").getString();
		EM_Settings.worldDelay = config.get(PhySetCat, "World Start Delay", 1000, "How long after world start until the physics system kicks in (DO NOT SET TOO LOW)").getInt(1000);
		EM_Settings.chunkDelay = config.get(PhySetCat, "Chunk Physics Delay", 500, "How long until individual chunk's physics starts after loading (DO NOT SET TOO LOW)").getInt(500);
		EM_Settings.physInterval = EM_Settings.physInterval >= 2 ? EM_Settings.physInterval : 2;
		EM_Settings.entityFailsafe = config.get(PhySetCat, "Physics entity fail safe level", 1, "0 = No action, 1 = Limit to < 100 per 8x8 block area, 2 = Delete excessive entities & Dump physics (EMERGENCY ONLY)").getInt(1);
		
		// Config Gas
		EM_Settings.noGases = config.get("Gases", "Disable Gases", false, "Disables all gases and slowly deletes existing pockets").getBoolean(false);
		EM_Settings.slowGases = config.get("Gases", "Slow Gases", false, "Normal gases will move extremely slowly and reduce TPS lag").getBoolean(false);
		EM_Settings.renderGases = config.get("Gases", "Render normal gas", false, "Whether to render gases not normally visible").getBoolean(false);
		EM_Settings.gasTickRate = config.get("Gases", "Gas Tick Rate", 256, "How many ticks between gas updates. Gas fires are 1/4 of this.").getInt(256);
		EM_Settings.gasPassLimit = config.get("Gases", "Gas Pass Limit", 2048, "How many gases can be processed in a single pass per chunk (-1 = infinite)").getInt(-1);
		EM_Settings.gasWaterLike = config.get("Gases", "Water like spreading", true, "Whether gases should spread like water (faster) or even out as much as possible (realistic)").getBoolean(true);
		
		// Potion ID's
		EM_Settings.hypothermiaPotionID = -1;
		EM_Settings.heatstrokePotionID = -1;
		EM_Settings.frostBitePotionID = -1;
		EM_Settings.dehydratePotionID = -1;
		EM_Settings.insanityPotionID = -1;
		
		EM_Settings.hypothermiaPotionID = config.get("Potions", "Hypothermia", nextAvailPotion(27)).getInt(nextAvailPotion(27));
		EM_Settings.heatstrokePotionID = config.get("Potions", "Heat Stroke", nextAvailPotion(28)).getInt(nextAvailPotion(28));
		EM_Settings.frostBitePotionID = config.get("Potions", "Frostbite", nextAvailPotion(29)).getInt(nextAvailPotion(29));
		EM_Settings.dehydratePotionID = config.get("Potions", "Dehydration", nextAvailPotion(30)).getInt(nextAvailPotion(30));
		EM_Settings.insanityPotionID = config.get("Potions", "Insanity", nextAvailPotion(31)).getInt(nextAvailPotion(31));
		
		// Multipliers ID's
		EM_Settings.tempMult = config.get("Speed Multipliers", "BodyTemp", 1.0D).getDouble(1.0D);
		EM_Settings.hydrationMult = config.get("Speed Multipliers", "Hydration", 1.0D).getDouble(1.0D);
		EM_Settings.airMult = config.get("Speed Multipliers", "AirQuality", 1.0D).getDouble(1.0D);
		EM_Settings.sanityMult = config.get("Speed Multipliers", "Sanity", 1.0D).getDouble(1.0D);
		
		EM_Settings.tempMult = EM_Settings.tempMult < 0 ? 0F : EM_Settings.tempMult;
		EM_Settings.hydrationMult = EM_Settings.hydrationMult < 0 ? 0F : EM_Settings.hydrationMult;
		EM_Settings.airMult = EM_Settings.airMult < 0 ? 0F : EM_Settings.airMult;
		EM_Settings.sanityMult = EM_Settings.sanityMult < 0 ? 0F : EM_Settings.sanityMult;
		
		// Config Options
		String ConSetCat = "Config";
		Property genConfig = config.get(ConSetCat, "Generate Blank Configs", false, "Will attempt to find and generate blank configs for any custom items/blocks/etc loaded before EnviroMine. Pack developers are highly encouraged to enable this! (Resets back to false after use)");
		if(!EM_Settings.genConfigs)
		{
			EM_Settings.genConfigs = genConfig.getBoolean(false);
		}
		genConfig.set(false);
		
		Property genDefault = config.get(ConSetCat, "Generate Defaults", true, "Generates EnviroMines initial default files");
		if(!EM_Settings.genDefaults)
		{
			EM_Settings.genDefaults = genDefault.getBoolean(true);
		}
		genDefault.set(false);
		
		EM_Settings.enableConfigOverride = config.get(ConSetCat, "Client Config Override (SMP)", false, "[DISABLED][WIP] Temporarily overrides client configurations with the server's (NETWORK INTESIVE!)").getBoolean(false);
		
		// Earthquake
		String EarSetCat = "Earthquakes";
		EM_Settings.enableQuakes = config.get(EarSetCat, "Enable Earthquakes", true).getBoolean(true);
		EM_Settings.quakePhysics = config.get(EarSetCat, "Triggers Physics", true, "Can cause major lag at times (Requires main physics to be enabled)").getBoolean(true);
		EM_Settings.quakeRarity = config.get(EarSetCat, "Rarity", 100).getInt(100);
		EM_Settings.quakeMode = config.get(EarSetCat, "Mode", 2, "Changes how quakes are created (-1 = random, 0 = wave normal, 1 = centre normal, 2 = centre tear, 3 = wave tear)").getInt(2);
		EM_Settings.quakeDelay = config.get(EarSetCat, "Tick delay", 10).getInt(10);
		EM_Settings.quakeSpeed = config.get(EarSetCat, "Speed", 2, "How many layers of rock it can eat through at a time").getInt(2);
		if(EM_Settings.quakeRarity < 0)
		{
			EM_Settings.quakeRarity = 0;
		}
		if(EM_Settings.quakeSpeed <= 0)
		{
			EM_Settings.quakeSpeed = 1;
		}
		
		// Easter Eggs!
		String eggCat = "Easter Eggs";
		EM_Settings.thingChance = config.getFloat("Cave Dimension Grue", eggCat, 0.000001F, 0F, 1F, "Chance the (extremely rare) grue in the cave dimension will attack in the dark (ignored on Halloween or Friday 13th)");
		
		// REMOVE OLD Settings if they exist
		// Sound
		if(config.hasCategory("Sound Options")) config.removeCategory(config.getCategory("Sound Options"));
		// Gui settings
		if(config.hasCategory("GUI Settings")) config.removeCategory(config.getCategory("GUI Settings"));
		
		config.save();

	}
	
	/**
	 * @deprecated Use config.getInt(...) instead as it provides min & max value caps
	 */
	@Deprecated
	private static int getConfigIntWithMinInt(Property prop, int min)
	{
		if (prop.getInt(min) >= min) {
			return prop.getInt(min);
		} else {
			prop.set(min);
			return min;
		}
	}
	
	static int nextAvailPotion(int startID)
	{
		for(int i = startID; i > 0; i++)
		{
			if(i == EM_Settings.hypothermiaPotionID || i == EM_Settings.heatstrokePotionID || i == EM_Settings.frostBitePotionID || i == EM_Settings.dehydratePotionID || i == EM_Settings.insanityPotionID)
			{
				continue;
			} else if(i >= Potion.potionTypes.length)
			{
				return i;
			} else if(Potion.potionTypes[i] == null)
			{
				return i;
			}
		}
		
		return startID;
	}
	
	//#######################################
	//#          Get File List              #                 
	//#This Grabs Directory List for Custom #
	//#######################################
	public static File[] GetFileList(String path)
	{
		
		// Will be used Auto Load Custom Objects from ??? Dir 
		File f = new File(path);
		File[] list = f.listFiles();
		
		return list;
	}
	
	private static boolean isCFGFile(File file)
	{
		String fileName = file.getName();
		
		if(file.isHidden()) return false;
		
		//Matcher
		String patternString = "(.*\\.cfg$)";
		
		Pattern pattern;
		Matcher matcher;
		// Make Sure its a .cfg File
		pattern = Pattern.compile(patternString);
		matcher = pattern.matcher(fileName);
		
		String MacCheck = ".DS_Store.cfg";
		
		if (matcher.matches() && matcher.group(0).toString().toLowerCase() == MacCheck.toLowerCase()) { return false;}
		
		return matcher.matches();
	}
	
	//###################################
	//#           Check Dir             #                 
	//#  Checks for, or makes Directory #
	//###################################	
	public static void CheckDir(File Dir)
	{
		boolean dirFlag = false;
		
		// create File object
		
		if(Dir.exists())
		{
			return;
		}
		
		try
		{
			dirFlag = Dir.mkdirs();
		} catch(Exception e)
		{
			EnviroMine.logger.log(Level.ERROR, "Error occured while creating config directory: " + Dir.getAbsolutePath(), e);
		}
		
		if(!dirFlag)
		{
			EnviroMine.logger.log(Level.ERROR, "Failed to create config directory: " + Dir.getAbsolutePath());
		}
	}
	
	/**
	 * Load Custom Objects          
	 * Used to Load Custom Blocks,Armor
	 * Entitys, & Items from Custom Config Dir        
	 */
	public static void LoadCustomObjects(File customFiles)
	{
		boolean datFile = isCFGFile(customFiles);
		
		// Check to make sure this is a Data File Before Editing
		if(datFile == true)
		{
			Configuration config;
			try
			{
				config = new Configuration(customFiles, true);
				
				//EnviroMine.logger.log(Level.INFO, "Loading Config File: " + customFiles.getAbsolutePath());
	
				config.load();


			// 	Grab all Categories in File
			List<String> catagory = new ArrayList<String>();
			Set<String> nameList = config.getCategoryNames();
			Iterator<String> nameListData = nameList.iterator();
			
			// add Categories to a List 
			while(nameListData.hasNext())
			{
				catagory.add(nameListData.next());
			}
			
			for(int x = 0; x < catagory.size(); x++)
			{
				String CurCat = catagory.get(x);
				
				if(!CurCat.isEmpty() && CurCat.contains(Configuration.CATEGORY_SPLITTER))
				{
					String parent = CurCat.split("\\" + Configuration.CATEGORY_SPLITTER)[0];
					
					if(propTypes.containsKey(parent) && propTypes.get(parent).useCustomConfigs())
					{
						PropertyBase property = propTypes.get(parent);
						property.LoadProperty(config, catagory.get(x));
					} else
					{
						EnviroMine.logger.log(Level.WARN, "Failed to load object " + CurCat);
					}
					
				}
			}
			
			config.save();
			
			// Add to list of loaded Config files
			loadedConfigs.add(config.getConfigFile().getName());
			
			} catch(Exception e)
			{
				e.printStackTrace();
				EnviroMine.logger.log(Level.ERROR, "FAILED TO LOAD CUSTOM CONFIG: " + customFiles.getName() + "\nNEW SETTINGS WILL BE IGNORED!", e);
				return;
			}
		}
			
			
	}
	
	public static ArrayList<String> getSubCategories(Configuration config, String mainCat)
	{
		ArrayList<String> category = new ArrayList<String>();
		Set<String> nameList = config.getCategoryNames();
		Iterator<String> nameListData = nameList.iterator();
		
		// add Categories to a List 
		while(nameListData.hasNext())
		{
			String catName = nameListData.next();
			
			if(catName.startsWith(mainCat + "."))
			{
				category.add(catName);
			}
		}
		
		return category;
	}
	
	public static void loadDefaultProperties()
	{
		Iterator<PropertyBase> iterator = propTypes.values().iterator();
		
		while(iterator.hasNext())
		{
			iterator.next().GenDefaults();
		}
	}

	
	public static String SaveMyCustom(String type, String name, Object[] data)
	{
		
		// Check to make sure this is a Data File Before Editing
		File configFile = new File(customPath + "MyCustom.cfg");
		
		//String canonicalName = data.getClass().getCanonicalName();
		//String classname;
		
		/*if (canonicalName == null) {
			classname = "Vanilla";
		} else
		{
			String[] classpath = canonicalName.toLowerCase().split("\\.");
			if (classpath[0].equalsIgnoreCase("net")) classname = "Vanilla";
			else classname = classpath[0];
		}*/
		
		Configuration config;
		try
		{
			config = new Configuration(configFile, true);
		} catch(NullPointerException e)
		{
			e.printStackTrace();
			EnviroMine.logger.log(Level.WARN, "FAILED TO SAVE NEW OBJECT TO MYCUSTOM.CFG");
			return "Failed to Open MyCustom.cfg";
		} catch(StringIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			EnviroMine.logger.log(Level.WARN, "FAILED TO SAVE NEW OBJECT TO MYCUSTOM.CFG");
			return "Failed to Open MyCustom.cfg";
		}
		
		config.load();
		
		String returnValue = "";
		
		if(type.equalsIgnoreCase("BLOCK"))
		{
			/*String nameULCat = blockCat + "." + name + " " + (Integer)data[1];
			
			if(config.hasCategory(nameULCat) == true)
			{
				config.removeCategory(config.getCategory(nameULCat));
				returnValue = "Removed";
			} else*/
			{
				//int metadata = (Integer)data[1];
				//BlockProperties.SaveProperty(config, nameULCat, (String)data[2], metadata, (String)data[2], metadata, 0, false, 0.00, 0.00, 0.00, "loose", false, false);
				BlockProperties.base.generateEmpty(config, Block.blockRegistry.getObject((String)data[2]));
				returnValue = "Saved";

			}
		} else if(type.equalsIgnoreCase("ENTITY"))
		{
			
			/*String nameEntityCat = entityCat + "." + name;
			
			if(config.hasCategory(nameEntityCat) == true)
			{
				config.removeCategory(config.getCategory(nameEntityCat));
				returnValue = "Removed";
			} else*/
			{
				//config.addCustomCategoryComment(nameEntityCat, classname + ":" + name);
				//EntityProperties.SaveProperty(config, nameEntityCat, (Integer)data[0], true, true, true, true, false, false, 0.0D, 0.0D, 37.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
				returnValue = "Saved";
			}
			
		} else if(type.equalsIgnoreCase("ITEM"))
		{
			/*String nameItemCat = itemsCat + "." + name;
			
			if(config.hasCategory(nameItemCat) == true)
			{
				config.removeCategory(config.getCategory(nameItemCat));
				returnValue = "Removed";
			} else*/
			{
				//config.addCustomCategoryComment(nameItemCat, classname + ":" + name);
				//ItemProperties.SaveProperty(config, nameItemCat, (String)data[0], (Integer)data[1], false, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 37.00);
				ItemProperties.base.generateEmpty(config, Item.itemRegistry.getObject((String)data[0]));
				returnValue = "Saved";
			}
			
		} else if(type.equalsIgnoreCase("ARMOR"))
		{
			// We can't remove configs as of yet through the new system. That will be up to the new UI
			/*String nameArmorCat = ArmorProperties.base.categoryName() + "." + name;
			
			if(config.hasCategory(nameArmorCat) == true)
			{
				config.removeCategory(config.getCategory(nameArmorCat));
				returnValue = "Removed";
			} else*/
			{
				ArmorProperties.base.generateEmpty(config, Item.itemRegistry.getObject((String)data[0]));
				returnValue = "Saved";
			}
		}
		
		config.save();
		
		
		return returnValue;
		
		//return null;
	}

} // End of Page

