package foxie.bettersleeping;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyBindingHandler {

	public static final KeyBinding sleepNow = new KeyBinding("key.khufu.sleepNow", Keyboard.KEY_Z, "key.categories.gameplay");

//	private static final KeyBinding[] bindings = new KeyBinding[] { sleepNow };

	public KeyBindingHandler() {
		
//		MinecraftForge.EVENT_BUS.register(this);
//		FMLCommonHandler.instance().bus().register(this);

		ClientRegistry.registerKeyBinding(sleepNow);
	}
	
}