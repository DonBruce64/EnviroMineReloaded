package enviromine;

import java.io.File;

import enviromine.systems.ConfigSystem;

/**Contains registration methods used by {@link EMINERegistry} and methods overridden by ClientProxy. 
 * See the latter for more info on overridden methods.
 * 
 * @author don_bruce
 */
public class CommonProxy{
	public void initConfig(File configFile){
		ConfigSystem.initCommon(configFile);
	}
}
