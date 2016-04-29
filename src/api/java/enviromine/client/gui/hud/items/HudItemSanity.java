package enviromine.client.gui.hud.items;

import org.lwjgl.opengl.GL11;

import enviromine.client.gui.Gui_EventManager;
import enviromine.client.gui.UI_Settings;
import enviromine.client.gui.hud.HUDRegistry;
import enviromine.client.gui.hud.HudItem;
import enviromine.core.EM_Settings;
import enviromine.utils.Alignment;
import enviromine.utils.RenderAssist;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class HudItemSanity extends HudItem	{

	@Override
	public String getName() {

		return "Sanity"; 
	}

	 public String getNameLoc()
	 {
		 return StatCollector.translateToLocal("options.enviromine.hud.sanity");	 
	 }
	
	@Override
	public String getButtonLabel() {

		return getNameLoc() +" Bar";
	}

	@Override
	public Alignment getDefaultAlignment() {

		return Alignment.BOTTOMRIGHT;
	}

	@Override
	public int getDefaultPosX() {

		return (((HUDRegistry.screenWidth - 4) - getWidth()));
	}

	@Override
	public int getDefaultPosY() {

		return (HUDRegistry.screenHeight - 30);
	}

	@Override
	public int getWidth() {

		return UI_Settings.minimalHud && !rotated ? 0 : 64;
	}

	@Override
	public int getHeight() {

		return 8;
	}

	@Override
    public boolean isEnabledByDefault() {
        return EM_Settings.enableSanity;
    }
  
	
	@Override
	public boolean isBlinking() {

		if(blink() && Gui_EventManager.tracker.sanity < 25)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public int getDefaultID() {

		return 2;
	}

	@Override
	public void render() 
	{
		GL11.glPushMatrix();
		
		float transx = (float)(this.posX - (this.posX * UI_Settings.guiScale));
		float transy = (float)(this.posY - (this.posY * UI_Settings.guiScale));
		
		GL11.glTranslated(transx, transy, 0);
		
		GL11.glScalef((float)UI_Settings.guiScale, (float)UI_Settings.guiScale, (float)UI_Settings.guiScale);
		
		int sanityBar = MathHelper.ceiling_float_int((Gui_EventManager.tracker.sanity / 100) * this.getWidth());
		
		int frameBorder = 4;
		if(this.isBlinking())
			frameBorder = 5;
		
		if(sanityBar > this.getWidth())
		{
			sanityBar = this.getWidth();
		} else if(sanityBar < 0)
		{
			sanityBar = 0;
		}
		
		if(!UI_Settings.minimalHud || rotated)
		{
			
			GL11.glPushMatrix();

			if(this.rotated)
			{
				int angle = -90;
				//int translateX = 0;
				//int translateY = 0;
				GL11.glTranslatef(posX,posY, 0);
				GL11.glRotatef( angle, 0, 0, 1 );
				GL11.glTranslatef(-posX + 6,-posY - 8 + (getWidth() /2), 0);
			}
			
			//Bar
			RenderAssist.drawTexturedModalRect(posX, posY, 0, 16, getWidth(), getHeight());
			RenderAssist.drawTexturedModalRect(posX, posY, 64, 16, sanityBar, getHeight());
			
			//render status update
			RenderAssist.drawTexturedModalRect(posX + sanityBar - 2, posY + 2, 28, 64, 4, 4);

			//Frame
			RenderAssist.drawTexturedModalRect(posX, posY, 0, getHeight() * frameBorder, getWidth(), getHeight());

			
			
			GL11.glPopMatrix();
		}
		
		if(UI_Settings.ShowGuiIcons == true)
		{
			int iconPosX = getIconPosX();
			if(rotated)
			{
				iconPosX = posX + 20;
			}
			// Render Icon
			RenderAssist.drawTexturedModalRect(iconPosX, posY - 4, 32, 80, 16, 16);
		}
		
		if(UI_Settings.ShowText == true  && !this.rotated)
		{
				//Render Text Frame
				RenderAssist.drawTexturedModalRect( getTextPosX(), posY, 64, getHeight() * 4, 32, getHeight());

				//Render Text
				//RenderAssist.drawTexturedModalRect(getTextPosX(), posY, 64, getHeight() * 4, 32, getHeight());
				Minecraft.getMinecraft().fontRenderer.drawString(Gui_EventManager.tracker.sanity + "%", getTextPosX(), posY, 16777215);
		}
		GL11.glPopMatrix();
	}

	@Override
	public ResourceLocation getResource(String type) 
	{
		if(type == "TintOverlay") return Gui_EventManager.blurOverlayResource;
		else return Gui_EventManager.guiResource;

	}

	@Override
	public void renderScreenOverlay(int scaledwidth, int scaledheight) {
		
		if(Gui_EventManager.tracker.sanity < 50F)
		{
			int grad = (int)((50F - Gui_EventManager.tracker.sanity) / 50 * 255F);
			RenderAssist.drawScreenOverlay(scaledwidth, scaledheight, RenderAssist.getColorFromRGBA(200, 0, 249, grad));
		}
	}



}
