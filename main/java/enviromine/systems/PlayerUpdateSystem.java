package enviromine.systems;

import java.util.HashMap;
import java.util.Map;

import enviromine.dataclasses.DamageSources;
import enviromine.dataclasses.PlayerProperties;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
	public static final Map<PlayerEntity, PlayerProperties> playerProperties = new HashMap<PlayerEntity, PlayerProperties>();
	
	@SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event){
		//Only calculate every second at the end of the tick.
		if(event.player.getEntityWorld().getGameTime()%20 == 1 && event.phase.equals(Phase.END)){
			PlayerEntity player = event.player;
	    	if(playerProperties.containsKey(player)){
		    	//Call all the tickers here to update player status.
		    	if(ConfigSystem.COMMON.isTemperatureEnabled.get()){
		    		playerTempTick(player, playerProperties.get(player));
		    	}
		    	if(ConfigSystem.COMMON.isHydrationEnabled.get()){
		    		playerHydrationTick(player, playerProperties.get(player));
		    	}
	    	}
		}
	}
    
    public static void playerTempTick(PlayerEntity player, PlayerProperties properties){

    }
	
    private static void playerHydrationTick(PlayerEntity player, PlayerProperties properties){
    	BlockPos playerPos = player.getPosition();
    	//Do basic water calcs.  If player is hot, increase water intake.
    	properties.hydration -= !ConfigSystem.COMMON.isTemperatureEnabled.get() || properties.temp < ConfigSystem.COMMON.hotTemp.get() ? ConfigSystem.COMMON.waterUsageIdle.get() : ConfigSystem.COMMON.waterUsageHot.get();
		
    	//Also if player is in the sun, and is not wearing shady armor, increase intake.
    	//Intake depends on biome temp, so cold biomes without shade aren't as bad as deserts.
    	if(ConfigSystem.COMMON.isTemperatureEnabled.get() && player.world.canBlockSeeSky(playerPos)){
    		//Brightness is from 0.0-1.0, where 1.0 is full sun.  Mobs burn at 0.5, so we use that here for overheating.
    		float brightness = player.getBrightness();
    		float temp = player.world.getBiome(playerPos).getTemperature(playerPos);
    		if(brightness > 0.5 && temp > ConfigSystem.COMMON.biomeHotTemp.get()){
    			properties.hydration -= (brightness - 0.5F)*(temp - ConfigSystem.COMMON.biomeHotTemp.get());
    		}
    	}
    	
    	//If player is dehydrated, apply potions.
    	//By default this is mining_fatigue, but this is configurable.
    	//Note that names here are yucky.  We have to get the potion by name,
    	//then iterate over all effect instances the potion has,
    	//and then make new instances with the correct duration.
    	if(properties.hydration < ConfigSystem.COMMON.dehydrationLevel.get()){
    		for(String potionName : ConfigSystem.COMMON.dehydrationEffects.get()){
    			//TODO potions don't apply effects?
    			for(EffectInstance effectInstance : Potion.getPotionTypeForName(potionName).getEffects()){
    				player.addPotionEffect(new EffectInstance(effectInstance.getPotion(), 50, 0));
    			}    			
    		}
    	}
    	
    	//Check if hydration is at 0, and if so, apply damage.
    	//Apply damage every 100 ticks, or 5 seconds.
    	if(properties.hydration < 0){
    		properties.hydration = 0;
    		if(ConfigSystem.COMMON.dehydrationDamage.get() > 0 && player.world.getGameTime()%100 < 20){
    			player.attackEntityFrom(new DamageSources.DamageSourceDehydration(), ConfigSystem.COMMON.dehydrationDamage.get());
    		}
    	}
    }
    
    @SubscribeEvent
    public static void loadPlayer(PlayerEvent.LoadFromFile event){
    	playerProperties.put(event.getPlayer(), new PlayerProperties(event.getPlayer().getPersistentData()));
    }
    
    @SubscribeEvent
    public static void savePlayer(PlayerEvent.SaveToFile event){
    	event.getPlayer().getPersistentData().put("emine", playerProperties.get(event.getPlayer()).getNBT());
    }
}
