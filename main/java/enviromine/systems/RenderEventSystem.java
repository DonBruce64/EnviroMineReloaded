package enviromine.systems;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import enviromine.EMINE;
import enviromine.dataclasses.PlayerProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**This class handles custom render changes.
 * Only runs on the client, unlike the other event systems.
 *
 * @author don_bruce
 */
@Mod.EventBusSubscriber(Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public final class RenderEventSystem{
	public static final int screenDefaultX = 854;
	public static final int screenDefaultY = 480;
	
	private static final ResourceLocation icons = new ResourceLocation(EMINE.MODID, "textures/hud/icons.png");
	
    /**
     * Renders meters for the four life parameters.
     */
    @SubscribeEvent()
    public static void on(RenderGameOverlayEvent.Pre event){
    	Minecraft mcInstance = Minecraft.getInstance();
    	if(event.getType().equals(RenderGameOverlayEvent.ElementType.HOTBAR)){
			boolean flashCue = mcInstance.world.getGameTime()%40 < 20;
	    	PlayerProperties properties = PlayerUpdateSystem.playerProperties.get(mcInstance.player);
	    	
	    	if(properties != null){
	    		mcInstance.getTextureManager().bindTexture(icons);
		    	GL11.glPushMatrix();
				GL11.glBegin(GL11.GL_QUADS);
		    	HudBarComponents.HYDRATION.render(event.getWindow().getScaledWidth()*0.01F, event.getWindow().getScaledHeight()*0.94F, event.getWindow().getScaledWidth()*0.25F, event.getWindow().getScaledHeight()*0.05F, properties.isDehydrated, properties.hydration < 15 && flashCue, properties.hydration/100D);
		    	GL11.glEnd();
		    	mcInstance.fontRenderer.drawString(((int) properties.hydration) + "%", (int) (event.getWindow().getScaledWidth()*0.20F), (int) (event.getWindow().getScaledHeight()*0.9525F), Color.WHITE.getRGB());
		    	GL11.glPopMatrix();
	    	}
    	}
    }
    
    private enum HudBarComponents{
    	TEMP(3, 4), HYDRATION(0, 4), AIR(1, 6), SANITY(2, 6);
    	
    	private final int barOffset;
    	private final int overlayOffset;
    	private static final double barHeight = 0.03125D;
    	
    	private HudBarComponents(int barOffset, int overlayOffset){
    		this.barOffset = barOffset;
    		this.overlayOffset = overlayOffset;
    	}
    	
    	public void render(double startingPosX, double startingPosY, double deltaX, double deltaY, boolean altBar, boolean altOverlay, double value){
    		double barUOffset = altBar ? 0.0D : 0.25D;
    		double barVOffset = barHeight*barOffset;
    		double overlayVOffset = barHeight*overlayOffset + (altOverlay ? barHeight : 0.0D);

    		//Render bar.
    		GL11.glTexCoord2d(barUOffset, barVOffset);
    		GL11.glVertex2d(startingPosX, startingPosY);
    		GL11.glTexCoord2d(barUOffset, barVOffset + barHeight);
    		GL11.glVertex2d(startingPosX, startingPosY + deltaY);
    		GL11.glTexCoord2d(barUOffset + 0.25D, barVOffset + barHeight);
    		GL11.glVertex2d(startingPosX + deltaX*value/1.5D, startingPosY + deltaY);
    		GL11.glTexCoord2d(barUOffset + 0.25D, barVOffset);
    		GL11.glVertex2d(startingPosX + deltaX*value/1.5D, startingPosY);
    		
    		
    		//Render overlay.
    		GL11.glTexCoord2d(0, overlayVOffset);
    		GL11.glVertex3d(startingPosX, startingPosY, 0);
    		GL11.glTexCoord2d(0, overlayVOffset + barHeight);
    		GL11.glVertex3d(startingPosX, startingPosY + deltaY, 0);
    		GL11.glTexCoord2d(0.375D, overlayVOffset + barHeight);
    		GL11.glVertex3d(startingPosX + deltaX, startingPosY + deltaY, 0);
    		GL11.glTexCoord2d(0.375D, overlayVOffset);
    		GL11.glVertex3d(startingPosX + deltaX, startingPosY, 0);
    	}
    }
}
