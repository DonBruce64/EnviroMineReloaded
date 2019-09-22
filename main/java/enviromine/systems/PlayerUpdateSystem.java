package enviromine.systems;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import enviromine.EMINE;
import enviromine.dataclasses.DamageSources;
import enviromine.dataclasses.EMINERegistry;
import enviromine.dataclasses.PlayerProperties;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome.Category;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**This class handles all tickable things that involve the player during gameplay.
 * This consists of hydration, temperature, and other status changes that need to
 * happen in real-time.  World interaction and rendering are done in their own
 * systems; this system is only for the four life parameters.  Updates here only
 * happen once a second rather than once a tick to save on CPU processing time.
 * This system should be looked at for the most current player property data.
 *
 * @author don_bruce
 */
@Mod.EventBusSubscriber
public final class PlayerUpdateSystem{
	public static final Map<PlayerEntity, PlayerProperties> playerProperties = new HashMap<PlayerEntity, PlayerProperties>();
	public static final Map<PlayerEntity, Integer> playersWhoFilledContainers = new HashMap<PlayerEntity, Integer>();
	public static final Map<PlayerEntity, Hand> playersWhoFilledBottles = new HashMap<PlayerEntity, Hand>();
	private static final Map<String, Effect> effectMap = new HashMap<String, Effect>();
	
	@SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event){
		//If we were flagged for picking up bad water, set it here.
		if(event.phase.equals(Phase.END)){
			if(!playersWhoFilledContainers.isEmpty()){
				for(Entry<PlayerEntity, Integer> playerEntry : playersWhoFilledContainers.entrySet()){
					PlayerEntity player = playerEntry.getKey();
					int lastFluidLevel = playerEntry.getValue();
					IFluidHandlerItem fluidHandler = player.getHeldItem(player.getActiveHand()).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
					int fluidChange =  fluidHandler.getFluidInTank(0).getAmount() - lastFluidLevel;
					//Check to see if we added water.  If so, we need to change it to something else.
					if(Fluids.WATER.equals(fluidHandler.getFluidInTank(0).getFluid()) && fluidChange > 0){
						//Drain the tank of water to get it back to its old level, then add bad water.
						//TODO this isn't working because I can't figure out how to make new fluids.
						if(Category.OCEAN.equals(player.world.getBiome(player.getPosition()).getCategory())){
							fluidHandler.drain(new FluidStack(Fluids.WATER, fluidChange), FluidAction.EXECUTE);
							fluidHandler.fill(new FluidStack(EMINERegistry.SALT_WATER, fluidChange), FluidAction.EXECUTE);
							System.out.println(fluidHandler.getFluidInTank(0).getFluid());
						}
					}
					
				}
				playersWhoFilledContainers.clear();
			}else if(!playersWhoFilledBottles.isEmpty()){
				for(Entry<PlayerEntity, Hand> playerEntry : playersWhoFilledBottles.entrySet()){
					PlayerEntity player = playerEntry.getKey();
					//If the player has a water bottle, they need checking.
					//if(player.getHeldItem(playerEntry.getValue()))
					if(Category.OCEAN.equals(player.world.getBiome(player.getPosition()).getCategory())){
						//Ocean.  Salt water potion should be in the player's hand rather than a water bottle.
						player.setHeldItem(playerEntry.getValue(), PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), EMINERegistry.SALT_WATER_POTION));
					}
				}
				playersWhoFilledBottles.clear();
			}
		}
		
		
		//Only calculate properties every second at the end of the tick.
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
    	
    	//If player is dehydrated, apply effects as specified in the config.
    	//These are active until the player is no longer dehydrated.
    	if(properties.hydration < ConfigSystem.COMMON.dehydrationLevel.get()){
    		for(String effectName : ConfigSystem.COMMON.dehydrationEffects.get()){
    			if(!effectMap.containsKey(effectName)){
    				if(ForgeRegistries.POTIONS.containsKey(new ResourceLocation(effectName))){
    					effectMap.put(effectName, ForgeRegistries.POTIONS.getValue(new ResourceLocation(effectName)));
    				}
    			}else{
    				player.addPotionEffect(new EffectInstance(effectMap.get(effectName), 100, 0));
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
    
    /**
	 * Create a new PlayerProperties class from player NBT, or a default one if the NBT is not present.
	 */
    @SubscribeEvent
    public static void loadPlayer(PlayerEvent.LoadFromFile event){
    	playerProperties.put(event.getPlayer(), new PlayerProperties(event.getPlayer().getPersistentData()));
    }
    
    /**
	 * Save data from PlayerProperties class to NBT.
	 */
    @SubscribeEvent
    public static void savePlayer(PlayerEvent.SaveToFile event){
    	event.getPlayer().getPersistentData().put(EMINE.MODID, playerProperties.get(event.getPlayer()).getNBT());
    }
    
    /**
	 * Resets data for the player if they die.
	 */
    @SubscribeEvent
    public static void respawnPlayer(PlayerEvent.PlayerRespawnEvent event){
    	if(!event.isEndConquered()){
    		playerProperties.get(event.getPlayer()).reset();
    	}
    }
}
