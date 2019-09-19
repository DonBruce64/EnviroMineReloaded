package enviromine.dataclasses;

import enviromine.systems.ConfigSystem;
import enviromine.systems.PlayerUpdateSystem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

/**This class is the main wrapper for Effects.  All effects share the properties of being harmful,
 * having the same color, and only running once a second to match update cycles of the main
 * PlayerUpdateSystem.  Because of this, effects are limited 5 seconds of duration to ensure
 * constant effect when active.
 * 
 *
 * @author don_bruce
 */
public abstract class EMINEEffects extends Effect{

	protected EMINEEffects(){
		super(EffectType.HARMFUL, 3694022);
	}
	
	@Override
	public boolean isReady(int duration, int amplifier){
		return duration%20 == 0;
	}
	
	@Override
	public void performEffect(LivingEntity livingEntity, int amplifier){
		if(PlayerUpdateSystem.playerProperties.containsKey((PlayerEntity) livingEntity)){
			performEffectOnPlayer((PlayerEntity) livingEntity);
		}
	}
		
	protected abstract void performEffectOnPlayer(PlayerEntity player); 
	
	
	public static class ThirstEffect extends EMINEEffects{
		protected void performEffectOnPlayer(PlayerEntity player){
			double currentHydration = PlayerUpdateSystem.playerProperties.get(player).hydration;
			double hydrationToLose = ConfigSystem.COMMON.waterUsageIdle.get()*10;
			PlayerUpdateSystem.playerProperties.get(player).hydration = currentHydration - hydrationToLose > 0 ? currentHydration - hydrationToLose : 0;
		}
	}
}
