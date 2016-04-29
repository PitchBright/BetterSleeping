package enviromine.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.client.gui.SaveController;
import enviromine.client.gui.hud.HUDRegistry;
import enviromine.core.EM_Settings;
import enviromine.gases.GasBuffer;
import enviromine.world.Earthquake;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class EM_ServerScheduledTickHandler
{
	@SubscribeEvent
	public void tickEnd(TickEvent.WorldTickEvent tick)
	{
		if(tick.side.isServer() && tick.phase == TickEvent.Phase.END)
		{
			GasBuffer.update();
			
			if(EM_Settings.enablePhysics)
			{
				EM_PhysManager.updateSchedule();
			}
			
			TorchReplaceHandler.UpdatePass();
			
			Earthquake.updateEarthquakes();
			
			if(EM_Settings.enableQuakes && MathHelper.floor_double(tick.world.getTotalWorldTime()/24000L) != Earthquake.lastTickDay && tick.world.playerEntities.size() > 0)
			{
				Earthquake.lastTickDay = MathHelper.floor_double(tick.world.getTotalWorldTime()/24000L);
				Earthquake.TickDay(tick.world);
			}
		}
	}
	
	// Used for to load up SaveContoler for clients side GUI settings
    //private boolean ticked = false;
    private boolean firstload = true;

    @SubscribeEvent
	@SideOnly(Side.CLIENT)
    public void RenderTickEvent(RenderTickEvent event) 
    {
        if ((event.type == Type.RENDER || event.type == Type.CLIENT) && event.phase == Phase.END) 
        {
            Minecraft mc = Minecraft.getMinecraft();
            if (firstload && mc != null) 
            {
                if (!SaveController.loadConfig(SaveController.UISettingsData))
                {
                    HUDRegistry.checkForResize();
                    HUDRegistry.resetAllDefaults();
                    SaveController.saveConfig(SaveController.UISettingsData);
                }
                firstload = false;
            }
        }
    }
}
