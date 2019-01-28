package enviromine.dataclasses;

import net.minecraft.nbt.NBTTagCompound;

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

	public PlayerProperties(NBTTagCompound playerNBT){
		if(playerNBT.hasKey("emine")){
			NBTTagCompound playerCompound = playerNBT.getCompoundTag("emine");
			this.isDehydrated = playerCompound.getBoolean("dehydrated");
			this.temp = playerCompound.getDouble("temp");
			this.hydration = playerCompound.getDouble("hydration");
		}else{
			this.temp = startingTemp;
			this.hydration = startingHydration;
		}
	}
	
	public NBTTagCompound getNBTTag(){
		NBTTagCompound emineTag = new NBTTagCompound();
		emineTag.setBoolean("isDehydrated", this.isDehydrated);
		emineTag.setBoolean("isOverheated", this.isOverheated);
		emineTag.setBoolean("isHypothermia", this.isHypothermia);
		emineTag.setDouble("temp", this.temp);
		emineTag.setDouble("hydration", this.hydration);
		return emineTag;
	}
}
