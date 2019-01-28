package enviromine.systems;

import java.util.HashMap;
import java.util.Map;

import enviromine.dataclasses.DamageSources;
import enviromine.dataclasses.PlayerProperties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**This class handles all tickable things that involve the player during gameplay.
 * This consists of hydration, temperature, and other status changes that need to
 * happen in real-time.  World interaction and rendering are done in their own
 * systems; this system is only for the four life parameters.  Updates here only
 * happen once a second rather than once a tick to save on CPU processing time.
 * This system should be looked at for the most current player property data.
 *
 * @author don_bruce
 */
@Mod.EventBusSubscriber()
public final class PlayerUpdateSystem{
	public static final Map<EntityPlayer, PlayerProperties> playerProperties = new HashMap<EntityPlayer, PlayerProperties>();
	
	private static boolean gotConfigs = false;
	
	private static int dehydrationLevel;
	private static int dehydrationDamage;
	
	private static double biomeHotTemp;
	private static double hotTemp;
	private static double overheatTemp;
	private static double waterUsageIdle;
	private static double waterUsageHot;
	
	private static String[] dehydrationEffects;
	
	@SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event){
		//Make sure we have the configs we need.  We can't make these static as they are loaded after class construction.
		if(!gotConfigs){
			dehydrationLevel = ConfigSystem.getIntegerConfig("DehydrationLevel");
			dehydrationDamage = ConfigSystem.getIntegerConfig("DehydrationDamage");
			biomeHotTemp = ConfigSystem.getDoubleConfig("BiomeHotTemp");
			hotTemp = ConfigSystem.getDoubleConfig("HotTemp");
			overheatTemp = ConfigSystem.getDoubleConfig("OverheatTemp");
			waterUsageIdle = ConfigSystem.getDoubleConfig("WaterUsageIdle");
			waterUsageHot = ConfigSystem.getDoubleConfig("WaterUsageHot");
			dehydrationEffects = ConfigSystem.getStringConfig("DehydrationEffects");
		}
		//Only calculate every second at the end of the tick.
		if(event.player.getEntityWorld().getTotalWorldTime()%20 == 1 && event.phase.equals(event.phase.END)){
			EntityPlayer player = event.player;
	    	if(playerProperties.containsKey(player)){
		    	//Call all the tickers here to update player status.
		    	if(ConfigSystem.isTemperatureEnabled){
		    		playerTempTick(player, playerProperties.get(player));
		    	}
		    	if(ConfigSystem.isHydrationEnabled){
		    		playerHydrationTick(player, playerProperties.get(player));
		    	}
	    	}
		}
	}
    
    public static void playerTempTick(EntityPlayer player, PlayerProperties properties){

    }
	
    private static void playerHydrationTick(EntityPlayer player, PlayerProperties properties){
    	BlockPos playerPos = player.getPosition();
		properties.hydration = 43;
    	//Do basic water calcs.  If player is hot, increase water intake.
    	properties.hydration -= !ConfigSystem.isTemperatureEnabled || properties.temp < hotTemp ? waterUsageIdle : waterUsageHot;
		
    	//Also if player is in the sun, and is not wearing shady armor, increase intake.
    	//Intake depends on biome temp, so cold biomes without shade aren't as bad as deserts.
    	if(ConfigSystem.isTemperatureEnabled && player.worldObj.canSeeSky(playerPos)){
    		//Brightness is from 0.0-1.0, where 1.0 is full sun.  Mobs burn at 0.5, so we use that here for overheating.
    		float brightness = player.getBrightness(1.0F);
    		float temp = player.worldObj.getBiome(playerPos).getFloatTemperature(playerPos);
    		if(brightness > 0.5 && temp > biomeHotTemp){
    			properties.hydration -= (brightness - 0.5F)*(temp - biomeHotTemp);
    		}
    	}
    	
    	//If player is dehydrated, apply potion effects.
    	if(properties.hydration < dehydrationLevel){
    		for(String potionName : dehydrationEffects){
    			player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation(potionName), 50, 0));
    		}
    	}
    	
    	//Check if hydration is at 0, and if so, apply damage.
    	if(properties.hydration < 0){
    		properties.hydration = 0;
    		if(dehydrationDamage > 0 && player.worldObj.getTotalWorldTime()%100 < 20){
    			player.attackEntityFrom(new DamageSources.DamageSourceDehydration(), dehydrationDamage);
    		}
    	}
    }
    
    @SubscribeEvent
    public static void loadPlayer(PlayerEvent.LoadFromFile event){
    	playerProperties.put(event.getEntityPlayer(), new PlayerProperties(event.getEntityPlayer().getEntityData()));
    }
    
    @SubscribeEvent
    public static void savePlayer(PlayerEvent.SaveToFile event){
    	event.getEntityPlayer().getEntityData().setTag("emine", playerProperties.get(event.getEntityPlayer()).getNBTTag());
    }
}
