package enviromine.systems;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**This class handles events that need to happen on cue.
 * Mainly for water bottles fill and drinking.
 *
 * @author don_bruce
 */
@Mod.EventBusSubscriber()
public final class ActionEventSystem{
	/**
	 * Intercept right-clicking with a bottle or container of water
	 * and ensure we return the proper water state.
	 */
	@SubscribeEvent
    public static void on(PlayerInteractEvent.RightClickBlock event){
		Block clickedBlock = event.getEntityPlayer().getEntityWorld().getBlockState(event.getPos()).getBlock();
		if(clickedBlock.equals(Blocks.WATER)){
			//TODO intercept item function and set new liquid.
		}
    }
	
	/**
	 * Intercept right-clicking with a bottle or container of water
	 * and ensure we return the proper water state.
	 */
	@SubscribeEvent
    public static void on(LivingEntityUseItemEvent.Finish event){
		if(event.getEntity() instanceof EntityPlayer){
			if(event.getItem() != null && event.getItem().getItem().equals(Items.POTIONITEM)){
				//TODO increase hydration and find a way to test if this is a water bottle.
			}
		}
    }
}
