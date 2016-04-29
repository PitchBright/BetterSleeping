package enviromine.client.gui.hud.items;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

public class HudItemTemperature extends HudItem
{
	
	@Override
	public String getName()
	{
		return "Temperature";
	}
	
	public String getNameLoc()
	{
		return StatCollector.translateToLocal("options.enviromine.hud.temperature");
	}
	
	@Override
	public String getButtonLabel()
	{
		return getNameLoc() + " Bar";
	}
	
	@Override
	public Alignment getDefaultAlignment()
	{
		return Alignment.BOTTOMLEFT;
	}
	
	@Override
	public int getDefaultPosX()
	{
		return 8;
	}
	
	@Override
	public int getDefaultPosY()
	{
		return(HUDRegistry.screenHeight - 30);
	}
	
	@Override
	public int getWidth()
	{
		return UI_Settings.minimalHud && !rotated ? 0 : 64;
	}
	
	@Override
	public int getHeight()
	{
		return 8;
	}
	
	@Override
	public boolean isEnabledByDefault()
	{
		return EM_Settings.enableBodyTemp;
	}
	
	@Override
	public int getDefaultID()
	{
		return 0;
	}
	
	@Override
	public boolean isBlinking()
	{
		if(blink() && Gui_EventManager.tracker.bodyTemp < 35 || blink() && Gui_EventManager.tracker.bodyTemp > 39)
		{
			return true;
		} else
		{
			return false;
		}
		
	}
	
	@Override
	public void render()
	{
		GL11.glPushMatrix();
		
		float transx = (float)(this.posX - (this.posX * UI_Settings.guiScale));
		float transy = (float)(this.posY - (this.posY * UI_Settings.guiScale));
		
		GL11.glTranslated(transx, transy, 0);
		
		GL11.glScalef((float)UI_Settings.guiScale, (float)UI_Settings.guiScale, (float)UI_Settings.guiScale);
		
		int heatBar = MathHelper.ceiling_float_int(((Gui_EventManager.tracker.bodyTemp + 50) / 150) * this.getWidth());
		int preheatBar = MathHelper.ceiling_float_int(((Gui_EventManager.tracker.airTemp + 50) / 150) * this.getWidth());
		int preheatIco = 16 - MathHelper.ceiling_float_int(((Gui_EventManager.tracker.airTemp + 50) / 150) * 16);
		
		float dispHeat = new BigDecimal(String.valueOf(Gui_EventManager.tracker.bodyTemp)).setScale(2, RoundingMode.DOWN).floatValue();
		float FdispHeat = new BigDecimal(String.valueOf((Gui_EventManager.tracker.bodyTemp * 1.8) + 32)).setScale(2, RoundingMode.DOWN).floatValue();
		
		int frameBorder = 4;
		if(this.isBlinking())
			frameBorder = 5;
		
		if(heatBar > getWidth())
			heatBar = getWidth();
		else if(heatBar < 0)
			heatBar = 0;
		
		if(preheatBar > getWidth())
			preheatBar = getWidth();
		else if(preheatBar < 0)
			preheatBar = 0;
		
		if(preheatIco > 24)
			preheatIco = 24;
		else if(preheatIco < 0)
			preheatIco = 0;
		
		if(!UI_Settings.minimalHud || rotated)
		{
			
			GL11.glPushMatrix();
			
			if(this.rotated)
			{
				int angle = -90;
				//int translateX = 0;
				//int translateY = 0;
				GL11.glTranslatef(posX, posY, 0);
				GL11.glRotatef(angle, 0, 0, 1);
				GL11.glTranslatef(-posX + 6, -posY - 8 + (getWidth() / 2), 0);
			}
			//heat Bar
			RenderAssist.drawTexturedModalRect(posX, posY, 0, 24, getWidth(), getHeight());
			
			//render status update
			RenderAssist.drawTexturedModalRect(posX + preheatBar - 4, posY, 32, 64, 8, 8);
			RenderAssist.drawTexturedModalRect(posX + heatBar - 2, posY + 2, 20, 64, 4, 4);
			
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
			RenderAssist.drawTexturedModalRect(iconPosX, posY - 4, 0, 80, 16, 16);
			
			// Render Icon Overlay
			if(preheatIco >= 8)
			{
				RenderAssist.drawTexturedModalRect(iconPosX, posY - 4 + preheatIco, 16, 96 + preheatIco, 16, 16 - preheatIco);
			} else
			{
				RenderAssist.drawTexturedModalRect(iconPosX, posY - 4 + preheatIco, 0, 96 + preheatIco, 16, 16 - preheatIco);
			}
			
		}
		
		if(UI_Settings.ShowText == true && !this.rotated)
		{
			//Render Text Frame
			RenderAssist.drawTexturedModalRect(getTextPosX(), posY, 64, getHeight() * 4, 32, getHeight());
			
			//Render Text
			if(UI_Settings.useFarenheit == true)
			{
				Minecraft.getMinecraft().fontRenderer.drawString(FdispHeat + "F", getTextPosX(), posY, 16777215);
			} else
			{
				Minecraft.getMinecraft().fontRenderer.drawString(dispHeat + "C", getTextPosX(), posY, 16777215);
			}
		}
		
		GL11.glPopMatrix();
	}
	
	@Override
	public ResourceLocation getResource(String type)
	{
		if(type == "TintOverlay")
			return Gui_EventManager.blurOverlayResource;
		else
			return Gui_EventManager.guiResource;
	}
	
	@Override
	public void renderScreenOverlay(int scaledwidth, int scaledheight)
	{
		
		if(Gui_EventManager.tracker.bodyTemp >= 39)
		{
			int grad = 0;
			if(Gui_EventManager.tracker.bodyTemp >= 43F)
			{
				grad = 255;
			} else
			{
				grad = MathHelper.floor_float((Gui_EventManager.tracker.bodyTemp - 39F) / 6F * 255F);
			}
			RenderAssist.drawScreenOverlay(scaledwidth, scaledheight, RenderAssist.getColorFromRGBA(255, 255, 255, grad));
			
		} else if(Gui_EventManager.tracker.bodyTemp <= 35)
		{
			int grad = 0;
			if(Gui_EventManager.tracker.bodyTemp <= 30F)
			{
				grad = 255;
			} else
			{
				grad = MathHelper.floor_float(Math.abs(5F - (Gui_EventManager.tracker.bodyTemp - 30F)) / 5F * 255F);
			}
			RenderAssist.drawScreenOverlay(scaledwidth, scaledheight, RenderAssist.getColorFromRGBA(125, 255, 255, grad));
		}
		
	}
	
}
