package enviromine.systems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.Mod;

/**This class handles events that need to happen on cue.
 * Used for items, this class is responsible for refilling the hydration of
 * a player when they drink a liquid, and ensuring that bottles and other containers
 * when filled with water are filled with the correct type of water for the biome they
 * are filled in.  This also ensures that if the player fills a bottle from those biomes,
 * the water in the bottle has the appropriate effects.
 *
 * @author don_bruce
 */
@Mod.EventBusSubscriber
public final class ActionEventSystem{
	/**
	 * Intercept right-clicking with a bottle or container and make sure that the
	 * water we get is the correct type.  This is done to prevent mucking around
	 * with worldgen.
	 */
	@SubscribeEvent
    public static void on(PlayerInteractEvent.RightClickItem event){
		if(Items.GLASS_BOTTLE.equals(event.getItemStack().getItem())){
			//Player used a glass bottle, presumably to fill it with water.
			//Will let the PlayerUpdateSystem check that, however.
			PlayerUpdateSystem.playersWhoFilledBottles.put(event.getPlayer(), event.getHand());
		}else{
			IFluidHandlerItem fluidHandler = event.getItemStack().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
			if(fluidHandler != null){
				//We are interacting with water with a liquid container.
				PlayerUpdateSystem.playersWhoFilledContainers.put(event.getPlayer(), fluidHandler.getFluidInTank(0).getAmount());
			}
		}
    }
	
	/**
	 * Intercept right-clicking with a drinkable substance to increase hydration.
	 * This is done to make the system compatible with any liquid from any mod.
	 */
	@SubscribeEvent
    public static void on(LivingEntityUseItemEvent.Finish event){
		if(!event.getEntity().world.isRemote){
			if(event.getEntity() instanceof PlayerEntity){
				if(event.getItem().getUseAction().equals(UseAction.DRINK)){
					if(ConfigSystem.COMMON.isHydrationEnabled.get()){
						double currentHydration = PlayerUpdateSystem.playerProperties.get(event.getEntity()).hydration;
						int hydrationToAdd = ConfigSystem.COMMON.itemHydration.get();
						PlayerUpdateSystem.playerProperties.get(event.getEntity()).hydration = currentHydration + hydrationToAdd < 100 ? currentHydration + hydrationToAdd : 100;
					}
				}
			}
		}
    }
}
