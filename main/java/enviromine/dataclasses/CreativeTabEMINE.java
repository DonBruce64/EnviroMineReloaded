package enviromine.dataclasses;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**Core mod creative tab class.  This is responsible for holding EMINE items/blocks
 * such as custom water bottles, the davy lamp, and various gas blocks.
 * 
 * @author don_bruce
 */
public final class CreativeTabEMINE extends CreativeTabs{
	
	public CreativeTabEMINE(){
		super("tabEMINE");
	}
	
	@Override
	public Item getTabIconItem(){
		return Items.APPLE;
		//TODO change this when we get the davyLamp in.
		//return EMINERegistry.davyLamp;
	}

	@Override
    @SideOnly(Side.CLIENT)
    public void displayAllRelevantItems(List<ItemStack> givenList){
		givenList.clear();
		for(Item item : EMINERegistry.itemList){
			for(CreativeTabs tab : item.getCreativeTabs()){
				if(tab.equals(this)){
					item.getSubItems(item, tab, givenList);
				}
			}
		}
    }
}
