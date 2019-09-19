package enviromine.systems;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import enviromine.EMINE;
import net.minecraftforge.common.ForgeConfigSpec;

/**Class that handles all configuration settings.
 * Methods are separated into client and server configs
 * for easier config file editing.
 * 
 * @author don_bruce
 */
public final class ConfigSystem{
	//Config variables for Forge systems.  Not used normally.
	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final ForgeConfigSpec COMMON_SPEC;
	
	//Actual classes for configs.  Reference for config values.
	public static final ClientConfig CLIENT;
	public static final CommonConfig COMMON;
	
	//This constructor creates all config variables and classes.
	//This is done when this class is first compiled as it's a static method.
	static{
		final Pair<ClientConfig, ForgeConfigSpec> specPairClient = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT = specPairClient.getLeft();
		CLIENT_SPEC = specPairClient.getRight();
		
		final Pair<CommonConfig, ForgeConfigSpec> specPairCommon = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
		COMMON = specPairCommon.getLeft();
		COMMON_SPEC = specPairCommon.getRight();
	}
	
	//Client config class.
	public static final class ClientConfig{
		final ForgeConfigSpec.BooleanValue testBoolean;
		
		ClientConfig(final ForgeConfigSpec.Builder builder) {
			builder.push("general");
			testBoolean = builder
					.comment("TESTING... 1...0...1...TESTING")
					.translation(EMINE.MODID + ".config.clientBoolean")
					.define("testBoolean", true);
			builder.pop();
		}
	}
	
	//Common config class.
	public static final class CommonConfig{
		//General configs.
		public final ForgeConfigSpec.BooleanValue isTemperatureEnabled;
		public final ForgeConfigSpec.BooleanValue isHydrationEnabled;
		
		//Temperature configs.
		public final ForgeConfigSpec.DoubleValue biomeHotTemp;
		public final ForgeConfigSpec.DoubleValue hotTemp;
		public final ForgeConfigSpec.DoubleValue overheatTemp;
		
		//Hydration configs.
		public final ForgeConfigSpec.DoubleValue waterUsageIdle;
		public final ForgeConfigSpec.DoubleValue waterUsageHot;
		public final ForgeConfigSpec.IntValue dehydrationLevel;
		public final ForgeConfigSpec.IntValue dehydrationDamage;
		public final ForgeConfigSpec.IntValue itemHydration;
		public final ForgeConfigSpec.IntValue thirstDuration;
		public final ForgeConfigSpec.ConfigValue<List<String>> dehydrationEffects;
		
		CommonConfig(final ForgeConfigSpec.Builder builder){
			builder.comment("General items.  These are to enable and disable systems globally.  System-specific configs will follow.");
			builder.push("general");
			isTemperatureEnabled = builder
				.comment("Enable temperature module?")
				.translation(EMINE.MODID + ".config.isTemperatureEnabled")
				.define("isTemperatureEnabled", true);
			isHydrationEnabled = builder
				.comment("Enable hydration module?")
				.translation(EMINE.MODID + ".config.isHydrationEnabled")
				.define("isHydrationEnabled", true);
			builder.pop();
			
			
			builder.comment("All config options here pertain to the temperature module.  \nIf this module is diabled, this section has no effect.");
			builder.push("temperature");
			biomeHotTemp = builder
				.comment("The biome temp at which biomes will be considered hot.  \nHot biomes will cause the player to become thirsty if they are out in the sun.  \nNote that temp gets colder as you get higher, so some biomes may be at both sides of this number depending on elevation.")
				.translation(EMINE.MODID + ".config.biomeHotTemp")
				.defineInRange("biomeHotTemp", 0.5D, 0.0D, 10.0D);
			hotTemp = builder
				.comment("The temp, in degrees F, the player needs to be to be considered hot.  \nWhen the player is hot they will require more water and get fatigued out in the sun.")
				.translation(EMINE.MODID + ".config.hotTemp")
				.defineInRange("hotTemp", 100D, 0.0D, 200D);
			overheatTemp = builder
				.comment("The temp, in degrees F, at which point the player begins to experience heatstroke.  \nPlayers at this temp will become dizzy and very weak.")
				.translation(EMINE.MODID + ".config.overheatTemp")
				.defineInRange("overheatTemp", 104D, 0.0D, 200D);
			builder.pop();
			
			
			builder.comment("All config options here pertain to the hydration module.  \nIf this module is diabled, this section has no effect.");
			builder.push("hydration");
			waterUsageIdle = builder
				.comment("How much water per second a player requires when in a cool biome.  \nThis is the minimum water usage regardless of temp or sunlight.")
				.translation(EMINE.MODID + ".config.waterUsageIdle")
				.defineInRange("waterUsageIdle", 0.0275D, 0.0D, 1.0D);
			waterUsageHot = builder
				.comment("How much water per second a player requires when they are hot.  \nThis is used in place of the regular water usage.  \nHot temp can be configured in the temp section.  If temp module is disabled, this does not apply.")
				.translation(EMINE.MODID + ".config.waterUsageHot")
				.defineInRange("waterUsageHot", 0.05D, 0.0D, 1.0D);
			dehydrationLevel = builder
				.comment("The percentage at which the player will experience the effects of dehydration.")
				.translation(EMINE.MODID + ".config.dehydrationLevel")
				.defineInRange("dehydrationLevel", 15, 0, 100);
			dehydrationDamage = builder
				.comment("How much damage to apply every 5 seconds when a player's thirst is at 0.  \nSet to 0 to disable damage at 0 thirst.")
				.translation(EMINE.MODID + ".config.dehydrationDamage")
				.defineInRange("dehydrationDamage", 1, 0, 100);
			itemHydration = builder
				.comment("How much hydration drinkable items restore.  Note that this works for mod items as well as regular water bottles and potions!")
				.translation(EMINE.MODID + ".config.itemHydration")
				.defineInRange("itemHydration", 10, 0, 100);
			thirstDuration = builder
				.comment("How many seconds the thirst effect should last when applied to a player.")
				.translation(EMINE.MODID + ".config.thirstDuration")
				.defineInRange("thirstDuration", 30, 0, 100);
			dehydrationEffects = builder
				.comment("A list of potions (effects) to apply to dehydrated players.  \nThis list should contain the names of potions that will be active when dehydrated.")
				.translation(EMINE.MODID + ".config.dehydrationEffects")
				.define("dehydrationEffects", Arrays.asList("mining_fatigue"));
			builder.pop();
		}
	}
}
