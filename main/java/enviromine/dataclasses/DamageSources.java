package enviromine.dataclasses;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

public abstract class DamageSources extends DamageSource{
	
	public DamageSources(String name, Entity playerResponsible){
		super(name);
	}
	
	public static class DamageSourceDehydration extends DamageSources{
		public DamageSourceDehydration(){
			super("dehydration", null);
		}
	};
}
