package enviromine.dataclasses;

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
	
	public boolean isDehydrated;
	public boolean isOverheated;
	public boolean isHypothermia;
	
	public double temp;
	public double hydration;

	public PlayerProperties(CompoundNBT playerNBT){
		if(playerNBT.contains("emine")){
			CompoundNBT emineNBT = playerNBT.getCompound("emine");
			this.isDehydrated = emineNBT.getBoolean("dehydrated");
			this.temp = emineNBT.getDouble("temp");
			this.hydration = emineNBT.getDouble("hydration");
		}else{
			this.temp = startingTemp;
			this.hydration = startingHydration;
		}
	}
	
	public CompoundNBT getNBT(){
		CompoundNBT emineNBT = new CompoundNBT();
		emineNBT.putBoolean("isDehydrated", this.isDehydrated);
		emineNBT.putBoolean("isOverheated", this.isOverheated);
		emineNBT.putBoolean("isHypothermia", this.isHypothermia);
		emineNBT.putDouble("temp", this.temp);
		emineNBT.putDouble("hydration", this.hydration);
		return emineNBT;
	}
}
