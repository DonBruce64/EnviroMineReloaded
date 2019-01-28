package enviromine.systems;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

/**Class that handles all configuration settings.
 * Methods are separated into client and server configs
 * for easier config file editing.
 * 
 * @author don_bruce
 */
public final class ConfigSystem{
	private static Configuration config;
	private static Map<String, Boolean> booleanConfigMap = new HashMap<String, Boolean>();
	private static Map<String, Integer> integerConfigMap = new HashMap<String, Integer>();
	private static Map<String, Double> doubleConfigMap = new HashMap<String, Double>();
	private static Map<String, String[]> stringConfigMap = new HashMap<String, String[]>();

	private static final String COMMON_CONFIG = "general";
	private static final String DAMAGE_CONFIG = "damage";
	private static final String TEMPERATURE_CONFIG = "temperature";
	private static final String HYDRATION_CONFIG = "hydration";
	private static final String CLIENT_CONFIG = "client";
	
	public static boolean isTemperatureEnabled;
	public static boolean isHydrationEnabled;
	
	
	public static void initCommon(File configFile){
		config = new Configuration(configFile);
		config.load();		
		isTemperatureEnabled = config.get(COMMON_CONFIG, "isTemperatureEnabled", true, "Enable temperature module?").getBoolean();
		isHydrationEnabled = config.get(COMMON_CONFIG, "EnableHydration", true, "Enable hydration module?").getBoolean();
		
		
		config.setCategoryComment(TEMPERATURE_CONFIG, "All config options here pertain to the temperature module.  \nIf this module is diabled, this section has no effect.");
		doubleConfigMap.put("BiomeHotTemp", config.get(TEMPERATURE_CONFIG, "BiomeHotTemp", 0.5F, "The biome temp at which biomes will be considered hot.  \nHot biomes will cause the player to become thirsty if they are out in the sun.  \nNote that temp gets colder as you get higher, so some biomes may be at both sides of this number depending on elevation.").getDouble());
		doubleConfigMap.put("HotTemp", config.get(TEMPERATURE_CONFIG, "HotTemp", 100D, "The temp, in degrees F, the player needs to be to be considered hot.  \nWhen the player is hot they will require more water and get fatigued out in the sun.").getDouble());
		doubleConfigMap.put("OverheatTemp", config.get(TEMPERATURE_CONFIG, "OverheatTemp", 102D, "The temp, in degrees F, at which point the player begins to experience heatstroke.  \nPlayers at this temp will become dizzy and very weak.").getDouble());
		
		
		config.setCategoryComment(HYDRATION_CONFIG, "All config options here pertain to the hydration module.  \nIf this module is diabled, this section has no effect.");
		doubleConfigMap.put("WaterUsageIdle", config.get(HYDRATION_CONFIG, "WaterUsageIdle", 0.0275D, "How much water per second a player requires when in a cool biome.  \nThis is the minimum water usage regardless of temp or sunlight.").getDouble());
		doubleConfigMap.put("WaterUsageHot", config.get(HYDRATION_CONFIG, "WaterUsageHot", 0.05D, "How much water per second a player requires when they are hot.  \nThis is used in place of the regular water usage.  \nHot temp can be configured in the temp section.  If temp module is disabled, this does not apply.").getDouble());
		integerConfigMap.put("DehydrationLevel", config.get(HYDRATION_CONFIG, "DehydrationLevel", 15, "The percentage at which the player will experience the effects of dehydration.").getInt());
		integerConfigMap.put("DehydrationDamage", config.get(HYDRATION_CONFIG, "DehydrationDamage", 1, "How much damage to apply every 5 seconds when a player's thirst is at 0.  \nSet to 0 to disable damage at 0 thirst.").getInt());
		stringConfigMap.put("DehydrationEffects", config.get(HYDRATION_CONFIG, "DehydrationEffects", new String[]{"mining_fatigue"}, "A list of potion effects to apply to dehydrated players.  \nThis list should contain the names of potions that will be active when dehydrated.").getStringList());
		
		config.save();
	}
	
	public static void initClient(File configFile){
		initCommon(configFile);
		config.save();
	}
	
	public static boolean getBooleanConfig(String configName){
		return booleanConfigMap.get(configName);
	}
	
	public static int getIntegerConfig(String configName){
		return integerConfigMap.get(configName);
	}

	public static double getDoubleConfig(String configName){
		return doubleConfigMap.get(configName);
	}
	
	public static String[] getStringConfig(String configName){
		return stringConfigMap.get(configName);
	}
	
	public static void setCommonConfig(String configName, Object value){
		setConfig(configName, String.valueOf(value), COMMON_CONFIG);
	}
	
	public static void setClientConfig(String configName, Object value){
		setConfig(configName, String.valueOf(value), CLIENT_CONFIG);
	}
	
	private static void setConfig(String configName, String value, String categoryName){
		ConfigCategory category = config.getCategory(categoryName);
		if(category.containsKey(configName)){
			if(booleanConfigMap.containsKey(configName)){
				booleanConfigMap.put(configName, Boolean.valueOf(value));
			}else if(integerConfigMap.containsKey(configName)){
				integerConfigMap.put(configName, Integer.valueOf(value));
			}else if(doubleConfigMap.containsKey(configName)){
				doubleConfigMap.put(configName, Double.valueOf(value));
			}else{
				return;
			}
			category.get(configName).set(value);
			config.save();
		}
	}
}
