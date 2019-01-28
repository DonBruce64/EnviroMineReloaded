package enviromine.dataclasses;

import java.lang.reflect.Field;

import enviromine.EMINE;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**Sub-class for client-only registration.  Used for models, item texture, entity rendering and the like.
 * Registrations are fired on events, so should be good for the next few MC versions.
 * 
 * @author don_bruce
 */
@Mod.EventBusSubscriber(Side.CLIENT)
public final class EMINERegistryClient{
	
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event){
		//Register the TESRs for blocks.
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFuelPump.class, new RenderFuelPump());
		
		//Register the item models.
		for(Field field : EMINERegistry.class.getFields()){
			if(field.getType().equals(Item.class)){
				try{
					Item item = (Item) field.get(null);
					ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(EMINE.MODID + ":" + item.getRegistryName().getResourcePath(), "inventory"));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
}
