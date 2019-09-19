package enviromine.dataclasses;

import net.minecraft.util.DamageSource;

/**This class is the main wrapper for DamageSources.  All DamageSources are contained here
 * and share the main methods of this class.  Actual sources are sub-classed for ease
 * of lookup.
 *
 * @author don_bruce
 */
public abstract class DamageSources extends DamageSource{
	
	public DamageSources(String name){
		super(name);
	}
	
	public static class DamageSourceDehydration extends DamageSources{
		public DamageSourceDehydration(){
			super("dehydration");
		}
	};
}
