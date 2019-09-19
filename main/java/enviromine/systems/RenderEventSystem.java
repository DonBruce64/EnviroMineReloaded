package enviromine.systems;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import enviromine.EMINE;
import enviromine.dataclasses.EMINERegistry;
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
	private static final int screenDefaultX = 854;
	private static final int screenDefaultY = 480;
	private static int scaledWidth;
	private static int scaledHeight;
	
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
	    		scaledWidth = event.getWindow().getScaledWidth();
	    		scaledHeight = event.getWindow().getScaledHeight();
	    		mcInstance.getTextureManager().bindTexture(icons);
		    	GL11.glPushMatrix();
				GL11.glBegin(GL11.GL_QUADS);
		    	HudBarComponents.HYDRATION.render(scaledWidth*0.01F, scaledHeight*0.94F, scaledWidth*0.25F, scaledHeight*0.05F, Minecraft.getInstance().player.getActivePotionEffect(EMINERegistry.THIRST) != null, properties.hydration < 15 && flashCue, properties.hydration/100D);
		    	GL11.glEnd();
		    	mcInstance.fontRenderer.drawString(((int) properties.hydration) + "%", (int) (scaledWidth*0.20F), (int) (scaledHeight*0.9525F), Color.WHITE.getRGB());
		    	GL11.glPopMatrix();
	    	}
    	}
    }
    
    /**
     * Enum for HUD bars.  Contains a generic method for rendering bars that allows for bar to be changed to
     * a different texture (renderBadStatBar) when a player is afflicted by a condition that affects this stat.
     * It also contains a boolean to render a different overlay when the stat itself is not in a good range
     * (renderLowStatBar).  To clarify, the former is for something that will cause the stat to change at a
     * faster than normal rate, while the latter indicates that the stat is not in the ideal range. 
     */
    private enum HudBarComponents{
    	TEMP(3, 4), HYDRATION(0, 4), AIR(1, 6), SANITY(2, 6);
    	
    	private final int barOffset;
    	private final int overlayOffset;
    	private static final double barHeight = 0.03125D;
    	
    	private HudBarComponents(int barOffset, int overlayOffset){
    		this.barOffset = barOffset;
    		this.overlayOffset = overlayOffset;
    	}
    	
    	public void render(double startingPosX, double startingPosY, double width, double height, boolean renderBadStatBar, boolean renderLowStatBar, double value){
    		double barUOffset = renderBadStatBar ? 0.0D : 0.25D;
    		double barVOffset = barHeight*barOffset;
    		double overlayVOffset = barHeight*overlayOffset + (renderLowStatBar ? barHeight : 0.0D);

    		//Render bar.
    		GL11.glTexCoord2d(barUOffset, barVOffset);
    		GL11.glVertex2d(startingPosX, startingPosY);
    		GL11.glTexCoord2d(barUOffset, barVOffset + barHeight);
    		GL11.glVertex2d(startingPosX, startingPosY + height);
    		GL11.glTexCoord2d(barUOffset + 0.25D, barVOffset + barHeight);
    		GL11.glVertex2d(startingPosX + width*value/1.5D, startingPosY + height);
    		GL11.glTexCoord2d(barUOffset + 0.25D, barVOffset);
    		GL11.glVertex2d(startingPosX + width*value/1.5D, startingPosY);
    		
    		
    		//Render overlay.
    		GL11.glTexCoord2d(0, overlayVOffset);
    		GL11.glVertex3d(startingPosX, startingPosY, 0);
    		GL11.glTexCoord2d(0, overlayVOffset + barHeight);
    		GL11.glVertex3d(startingPosX, startingPosY + height, 0);
    		GL11.glTexCoord2d(0.375D, overlayVOffset + barHeight);
    		GL11.glVertex3d(startingPosX + width, startingPosY + height, 0);
    		GL11.glTexCoord2d(0.375D, overlayVOffset);
    		GL11.glVertex3d(startingPosX + width, startingPosY, 0);
    	}
    }
}
