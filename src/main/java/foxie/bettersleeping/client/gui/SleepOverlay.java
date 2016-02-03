package foxie.bettersleeping.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import baubles.api.BaublesApi;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import foxie.bettersleeping.BetterSleeping;
import foxie.bettersleeping.Config;
import foxie.bettersleeping.item.ItemClass;
import foxie.bettersleeping.logic.MinecraftTime;

public class SleepOverlay extends OptionalGuiOverlay {
   public static final int BTN_WIDTH  = 5;
   public static final int BAR_WIDTH  = 182;
   public static final int MAX_OFFSET = BAR_WIDTH - BTN_WIDTH;
   public static final int BAR_HEIGHT = 5;

   public static final int ICON_WIDTH  = 0;
   public static final int ICON_HEIGHT = 0;

   public static long sleepCounter = 0;

   @SubscribeEvent
   public void onGuiRender(RenderGameOverlayEvent event) {
      OpenGlHelper.glBlendFunc(770, 771, 0, 1);

      if ((event.type != RenderGameOverlayEvent.ElementType.ALL) ||
              event.isCancelable()) {
         return;
      }

      Minecraft mc = FMLClientHandler.instance().getClient();
      ScaledResolution scaleRes = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
      int width = scaleRes.getScaledWidth();
	  int height = scaleRes.getScaledHeight();
      
      TextureManager mgr = Minecraft.getMinecraft().renderEngine;
      mgr.bindTexture(new ResourceLocation(BetterSleeping.MODID, "textures/gui/bar.png"));
      
      int k = (width - BAR_WIDTH) / 2;
	  int l = (height - BAR_HEIGHT) / 2 + (height/2 - 26);
      drawTexturedModalRect(k, l, 0, 0, BAR_WIDTH, BAR_HEIGHT);
      
//      drawTexturedModalRect(Config.guiOffsetLeft, Config.guiOffsetTop, 0, 0, BAR_WIDTH, BAR_HEIGHT);
//      drawTexturedModalRect(0, 0, 0, 69, 182, 5);

//      int takenPercent = (int) (((double) sleepCounter / Config.maximumSleepCounter) * MAX_OFFSET);
      int barRemaining = (int) (((double) sleepCounter / Config.maximumSleepCounter) * 182); 
//      System.out.println(sleepCounter);
//      if (takenPercent > MAX_OFFSET)
//         takenPercent = MAX_OFFSET;

//      drawTexturedModalRect(k, l, 0, 8, BTN_WIDTH, BAR_HEIGHT);
      drawTexturedModalRect(k, l, 0, 8, barRemaining, BAR_HEIGHT);
      
      
//      ItemStack bed = new ItemStack(Items.bed);
//
//      mgr.bindTexture(TextureMap.locationItemsTexture);
//      drawTexturedModelRectFromIcon(Config.guiOffsetLeft + BAR_WIDTH + 4, Config.guiOffsetTop - ((ICON_HEIGHT - BAR_HEIGHT) / 2),
//              Items.bed.getIcon(bed, 1), ICON_WIDTH, ICON_HEIGHT);

      renderTimeOverlay();

   }

   @Optional.Method(modid = "Baubles|API")
   @Override
   public void renderTimeOverlay() {
      if (Config.enableRingWatch) {
         IInventory baubles = BaublesApi.getBaubles(Minecraft.getMinecraft().thePlayer);
         if (baubles == null)
            return; // no idea why that happens? When baubles is installed the player should have it... maybe sync issue? meh

         for (int i = 0; i < baubles.getSizeInventory(); i++) {
            ItemStack itemStack = baubles.getStackInSlot(i);
            if (itemStack == null)
               continue;

            if (itemStack.getItem() == ItemClass.itemRingWatch) {
               MinecraftTime time = MinecraftTime.getFromWorldTime(Minecraft.getMinecraft().theWorld.getWorldTime());
               drawCenteredString(Minecraft.getMinecraft().fontRenderer, time.toString(), Config.guiOffsetLeft + BAR_WIDTH / 2,
                       Config.guiOffsetTop + 16, 0xFFFFFF);
               return;
            }
         }
      }
   }
}
