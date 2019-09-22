package enviromine.dataclasses;

import enviromine.EMINE;
import net.minecraft.nbt.CompoundNBT;

/**This class is the structure for player runtime properties like hydration,
 * temperature, and sanity, as well as status effect like frostbite and
 * overheat.  One of these classes is mapped to every player and saved
 * to their NBT whenever the world is saved.
 *
 * @author don_bruce
 */
public final class PlayerProperties{
	private static final double startingTemp = 98.6D;
	private static final double startingHydration = 100D;
	
	public double temp;
	public double hydration;

	public PlayerProperties(CompoundNBT playerNBT){
		if(playerNBT.contains(EMINE.MODID)){
			CompoundNBT emineNBT = playerNBT.getCompound(EMINE.MODID);
			this.temp = emineNBT.getDouble("temp");
			this.hydration = emineNBT.getDouble("hydration");
		}else{
			reset();
		}
	}
	
	public CompoundNBT getNBT(){
		CompoundNBT emineNBT = new CompoundNBT();
		emineNBT.putDouble("temp", this.temp);
		emineNBT.putDouble("hydration", this.hydration);
		return emineNBT;
	}
	
	public void reset(){
		this.temp = startingTemp;
		this.hydration = startingHydration;
	}
}
