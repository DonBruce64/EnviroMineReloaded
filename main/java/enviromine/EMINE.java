package enviromine;

import org.apache.logging.log4j.Logger;

import enviromine.dataclasses.EMINERegistry;
import enviromine.systems.ArmorSystem;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = EMINE.MODID, name = EMINE.MODNAME, version = EMINE.MODVER)
public class EMINE{
	public static final String MODID="enviromine";
	public static final String MODNAME="EnviroMine";
	public static final String MODVER="2.0.0";
	
	
	
	@Instance(value = EMINE.MODID)
	public static EMINE instance;
	public static Logger EMINELog;
	public static final SimpleNetworkWrapper EMINENet = NetworkRegistry.INSTANCE.newSimpleChannel("EMINENet");
	@SidedProxy(clientSide="enviromine.ClientProxy", serverSide="enviromine.CommonProxy")
	public static CommonProxy proxy;
	
	public EMINE(){
		FluidRegistry.enableUniversalBucket();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		EMINELog = event.getModLog();
		proxy.initConfig(event.getSuggestedConfigurationFile());
		ArmorSystem.init(event.getModConfigurationDirectory());
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		EMINERegistry.init();
	}
}
