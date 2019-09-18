package enviromine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import enviromine.systems.ConfigSystem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(value = EMINE.MODID)
public class EMINE{
	public static final String MODID = "enviromine";

	public static Logger EMINELog = LogManager.getLogger();
	//public static final SimpleNetworkWrapper EMINENet = NetworkRegistry.INSTANCE.newSimpleChannel("EMINENet");
	//@SidedProxy(clientSide="enviromine.ClientProxy", serverSide="enviromine.CommonProxy")
	//public static CommonProxy proxy;

	//This runs once Forge constructs our mod.
	//Setup things to be done before Forge is fully-started belong here!
	public EMINE(){
		//Add listeners for setup functions.
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupCommon);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        MinecraftForge.EVENT_BUS.register(this);
        
        //Init Configs in ConfigSystem.
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigSystem.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigSystem.COMMON_SPEC);
    }
	
	//Common setup.  Pre-init and comes after registry events.
	private void setupCommon(final FMLCommonSetupEvent event){
		//EMINERegistry.init();
		//ArmorSystem.init(event.getModConfigurationDirectory());
	}
	
	//Client setup.  Fired after common.  Should setup graphical rendering and configs.
	//TODO we may not need this if we can only use events...
	private void setupClient(final FMLClientSetupEvent event){
    }
}
