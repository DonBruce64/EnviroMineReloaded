package enviromine.systems;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import enviromine.EMINE;
import net.minecraft.util.ResourceLocation;

/**This class is responsible for cataloging all the ways that armor
 * affects the player.  It can be queried to detect if a specific piece of armor
 * affects a player's hydration, temperature, or air quality stats.
 * All parameters come from JSON files that are present in the config folder.
 * If no config file is present for a piece of armor, the default values are used.
 *
 * @author don_bruce
 */
public final class ArmorSystem{
	private static final Map<ResourceLocation, ArmorProperties> armorProperties = new HashMap<ResourceLocation, ArmorProperties>();
	private static final ArmorProperties defaultProperties = new ArmorProperties(false, false, 0.0F, 1.0F);
	
	public static ArmorProperties getArmorProperties(ResourceLocation armorRegistryName){
		return armorProperties.containsKey(armorRegistryName) ? armorProperties.get(armorRegistryName) : defaultProperties;
	}
	
	public static void init(File mainConfigDir){
		//First load config files.
		File emineConfigDir = new File(mainConfigDir.getAbsolutePath() + File.separatorChar + "emine");
		if(!emineConfigDir.exists()){
			emineConfigDir.mkdirs();
		}
		File armorConfigDir = new File(emineConfigDir.getAbsolutePath() + File.separatorChar + "armor");
		if(!armorConfigDir.exists()){
			armorConfigDir.mkdirs();
		}
		for(File armorDir : armorConfigDir.listFiles()){
			//We will have mod-specific files in this dir.
			//Loop through them to get all the armor registered.
			if(armorDir.isDirectory()){
				String modName = armorDir.getName().substring(0, armorDir.getName().length() - ".json".length());
				for(File armorFile : armorDir.listFiles()){
					try{
						String armorName = armorFile.getName().substring(0, armorFile.getName().length() - ".json".length());
						FileReader armorReader = new FileReader(armorFile);
						ArmorProperties property =  new Gson().fromJson(armorReader, ArmorProperties.class);
						armorProperties.put(new ResourceLocation(modName, armorName), property);
					}catch(Exception e){
						EMINE.EMINELog.error("AN ERROR WAS ENCOUNTERED WHEN TRYING TO PARSE: " + armorFile.getAbsolutePath());
						EMINE.EMINELog.error(e.getMessage());
					}
				}
			}
		}
		
		//Now go in reverse, ensuring all armor that is in-game is in the configs.
		//We only do vanilla armor here, as mod armor may not be registered yet and it's
		//excess files to clog up things.
		final String[] vanillaArmorNames = new String[]{
			"leather_helmet", "leather_chestplate", "leather_leggings", "leather_boots", 
			"chainmail_helmet", "chainmail_chestplate", "chainmail_leggings", "chainmail_boots",
		    "iron_helmet", "iron_chestplate", "iron_leggings" ,"iron_boots",
		    "diamond_helmet", "diamond_chestplate", "diamond_leggings", "diamond_boots",
		    "golden_helmet", "golden_chestplate", "golden_leggings", "golden_boots"
		};		
		File vanillaArmorDir = new File(armorConfigDir.getAbsolutePath() + File.separatorChar + "minecraft");
		for(String armorName : vanillaArmorNames){
			File vanillaArmorFile = new File(vanillaArmorDir.getAbsolutePath() + File.separatorChar + armorName);
			if(!vanillaArmorFile.exists()){
				try{
					final boolean providesShade = armorName.endsWith("helmet");
					final float insulation = armorName.startsWith("leather") ? 0.15F : 0.05F;
					final float heatFactor = armorName.startsWith("leather") ? 0.05F : 0.10F;
					ArmorProperties vanillaProperties = new ArmorProperties(providesShade, false, insulation, heatFactor);
					armorProperties.put(new ResourceLocation(armorName), vanillaProperties);
					FileWriter writer = new FileWriter(vanillaArmorFile);
					new Gson().toJson(vanillaProperties, writer);
					writer.close();
				}catch (IOException e){
					EMINE.EMINELog.error("AN ERROR WAS ENCOUNTERED WHEN TRYING TO CREATE DEFAULT VANILLA CONFIG: " + armorName);
					EMINE.EMINELog.error(e.getMessage());
				}
			}
		}
	}
	
	public static final class ArmorProperties{
		public final boolean providesShade;
		public final boolean providesAir;
		public final float insulation;
		public final float heatFactor;
		
		public ArmorProperties(boolean providesShade, boolean providesAir, float insulation, float heatFactor){
			this.providesShade = providesShade;
			this.providesAir = providesAir;
			this.insulation = insulation;
			this.heatFactor = heatFactor;
		}
	}
}
