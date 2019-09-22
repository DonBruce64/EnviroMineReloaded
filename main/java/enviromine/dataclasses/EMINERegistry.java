package enviromine.dataclasses;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryManager;

/**Main registry class.  This class should be referenced by any class looking for
 * enviromine registerable objects.  Adding new objects is a simple as adding them
 * as a field; events will do the rest through reflection.  This is a bit more concice
 * than the newer way of using ObjectHolder references as there's only one area where the
 * object needs to be added.
 * 
 * @author don_bruce
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EMINERegistry{	
	//Blocks
	
	//Items
	
	//Effects
	public static final Effect THIRST = new EMINEEffects.ThirstEffect();
	
	//Fluids
	public static final Fluid SALT_WATER = new WaterFluid.Source(){
		@Override
		public Fluid getFlowingFluid(){
			return SALT_WATER;
		}
		
		@Override
		public Fluid getStillFluid(){
			return SALT_WATER;
		}
	};
	
	//Potions
	public static final Potion SALT_WATER_POTION = new Potion(new EffectInstance(THIRST, 600, 0, false, false));
	
	//Counters for registry systems.
	private static int packetNumber = 0;
	
	
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
					event.getRegistry().register(block.setRegistryName(name));
					
					//TODO do we need to register TEs anymore?
					/*
					if(block instanceof ITileEntityProvider){
						Class<? extends TileEntity> tileEntityClass = ((ITileEntityProvider) block).createNewTileEntity(null, 0).getClass();
						if(!registeredTileEntityClasses.contains(tileEntityClass)){
							GameRegistry.registerTileEntity(tileEntityClass, tileEntityClass.getSimpleName());
							registeredTileEntityClasses.add(tileEntityClass);
						}
					}*/
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
						event.getRegistry().register(item.setRegistryName(name));
					}else{
						name = name.substring("itemblock".length());
						event.getRegistry().register(item.setRegistryName(name));
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	

	@SubscribeEvent
	public static void registerEffects(RegistryEvent.Register<Effect> event){
		for(Field field : EMINERegistry.class.getFields()){
			if(field.getType().equals(Effect.class)){
				try{
					Effect effect = (Effect) field.get(null);
					effect.setRegistryName(field.getName().toLowerCase());
					event.getRegistry().register(effect);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void registerFluids(RegistryEvent.Register<Fluid> event){
		for(Field field : EMINERegistry.class.getFields()){
			if(field.getType().equals(Fluid.class)){
				try{
					Fluid fluid = (Fluid) field.get(null);
					fluid.setRegistryName(field.getName().toLowerCase());
					event.getRegistry().register(fluid);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	@SubscribeEvent
	public static void registerPotions(RegistryEvent.Register<Potion> event){
		for(Field field : EMINERegistry.class.getFields()){
			if(field.getType().equals(Potion.class)){
				try{
					Potion potion = (Potion) field.get(null);
					potion.setRegistryName(field.getName().toLowerCase());
					event.getRegistry().register(potion);
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
		//TODO make this use JSON.
		//GameRegistry.addShapedRecipe(output, params);
		//++craftingNumber;
	}
	
	/**
	 * Registers a packet and its handler on the client and/or the server.
	 * @param packetClass
	 * @param handlerClass
	 * @param client
	 * @param server
	 */
	//private static <REQ extends IMessage, REPLY extends IMessage> void registerPacket(Class<REQ> packetClass, Class<? extends IMessageHandler<REQ, REPLY>> handlerClass, boolean client, boolean server){
//		if(client)EMINE.EMINENet.registerMessage(handlerClass, packetClass, ++packetNumber, Side.CLIENT);
	//	if(server)EMINE.EMINENet.registerMessage(handlerClass, packetClass, ++packetNumber, Side.SERVER);
	//}
}
