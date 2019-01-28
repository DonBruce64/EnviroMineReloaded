package enviromine.dataclasses;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import enviromine.EMINE;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

/**Main registry class.  This class should be referenced by any class looking for
 * EMINE items or blocks.  Adding new items and blocks is a simple as adding them
 * as a field; the init method automatically registers all items and blocks in the class
 * and orders them according to the order in which they were declared.
 * 
 * @author don_bruce
 */
@Mod.EventBusSubscriber
public final class EMINERegistry{
	/**All registered items are stored in this list as they are added.  Used to sort items in the creative tab.**/
	public static List<Item> itemList = new ArrayList<Item>();
	
	/**Creative tab for items**/
	public static final CreativeTabEMINE coreTab = new CreativeTabEMINE();

	//Items
	//public static final Item manual = new ItemManual().setCreativeTab(coreTab);
	
	//Blocks
	//public static final Block vehicleBench = new BlockPartBench("plane", "car");
	
	//Fuel pump.
	//public static final Block fuelPump = new BlockFuelPump().setCreativeTab(coreTab);		
	//public static final Item itemBlockFuelPump = new ItemBlock(fuelPump);
	
	//Counters for registry systems.
	private static int packetNumber = 0;
	private static int craftingNumber = 0;
	
	
	/**All run-time things go here.**/
	public static void init(){
		initPackets();
		initItemRecipes();
	}
	
	/**
	 * Registers all blocks present in this class.
	 * Also adds the respective TileEntity if the block has one.
	 */
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event){
		//Need to keep track of which TE classes we've registered so we don't double-register them for blocks that use the same TE.
		List<Class<? extends TileEntity>> registeredTileEntityClasses = new ArrayList<Class<? extends TileEntity>>();
		for(Field field : EMINERegistry.class.getFields()){
			if(field.getType().equals(Block.class)){
				try{
					Block block = (Block) field.get(null);
					String name = field.getName().toLowerCase();
					event.getRegistry().register(block.setRegistryName(name).setUnlocalizedName(name));
					if(block instanceof ITileEntityProvider){
						Class<? extends TileEntity> tileEntityClass = ((ITileEntityProvider) block).createNewTileEntity(null, 0).getClass();
						if(!registeredTileEntityClasses.contains(tileEntityClass)){
							GameRegistry.registerTileEntity(tileEntityClass, tileEntityClass.getSimpleName());
							registeredTileEntityClasses.add(tileEntityClass);
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Registers all items (and itemblocks) present in this class.
	 */
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event){
		//First register all core items.
		for(Field field : EMINERegistry.class.getFields()){
			if(field.getType().equals(Item.class)){
				try{
					Item item = (Item) field.get(null);
					String name = field.getName().toLowerCase();
					if(!name.startsWith("itemblock")){
						event.getRegistry().register(item.setRegistryName(name).setUnlocalizedName(name));
						EMINERegistry.itemList.add(item);
					}else{
						name = name.substring("itemblock".length());
						event.getRegistry().register(item.setRegistryName(name).setUnlocalizedName(name));
						EMINERegistry.itemList.add(item);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void initPackets(){
		//registerPacket(AileronPacket.class, AileronPacket.Handler.class, true, true);
	}
	
	private static void initItemRecipes(){
		//Fuel pump
		/*registerRecipe(new ItemStack(itemBlockFuelPump),
				"DED",
				"CBC",
				"AAA",
				'A', new ItemStack(Blocks.STONE_SLAB, 1, 0),
				'B', Items.IRON_INGOT,
				'C', new ItemStack(Items.DYE, 1, 0),
				'D', new ItemStack(Items.DYE, 1, 1),
				'E', Blocks.GLASS_PANE);*/
	}
	
	/**Registers a crafting recipe.  This is segmented out here as the method changes in 1.12 and the single location makes it easy for the script to update it.**/
	private static void registerRecipe(ItemStack output, Object...params){
		GameRegistry.addShapedRecipe(output, params);
		++craftingNumber;
	}
	
	/**
	 * Registers a packet and its handler on the client and/or the server.
	 * @param packetClass
	 * @param handlerClass
	 * @param client
	 * @param server
	 */
	private static <REQ extends IMessage, REPLY extends IMessage> void registerPacket(Class<REQ> packetClass, Class<? extends IMessageHandler<REQ, REPLY>> handlerClass, boolean client, boolean server){
		if(client)EMINE.EMINENet.registerMessage(handlerClass, packetClass, ++packetNumber, Side.CLIENT);
		if(server)EMINE.EMINENet.registerMessage(handlerClass, packetClass, ++packetNumber, Side.SERVER);
	}
}
