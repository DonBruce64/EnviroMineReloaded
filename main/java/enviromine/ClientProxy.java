package enviromine;

import java.io.File;

import enviromine.systems.ConfigSystem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**Class responsible for performing client-only updates and operations.
 * This class acts as a forwarding system rather than code executor.
 * Code operations should be in their own classes, if possible.
 * 
 * @author don_bruce
 */
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy{
	
	@Override
	public void initConfig(File configFile){
		ConfigSystem.initClient(configFile);
	}
}
