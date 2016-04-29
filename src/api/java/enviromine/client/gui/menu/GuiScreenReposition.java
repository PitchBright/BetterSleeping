package enviromine.client.gui.menu;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.client.gui.SaveController;
import enviromine.client.gui.hud.HudItem;
import enviromine.utils.Alignment;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class GuiScreenReposition extends GuiScreen {
	protected GuiScreen parentScreen;
	protected HudItem hudItem;
	protected boolean axisAlign;
	protected int oldPosX;
	protected int oldPosY;
	private static boolean help = true;

	public GuiScreenReposition(GuiScreen parentScreen, HudItem hudItem) {
		this.parentScreen = parentScreen;
		this.hudItem = hudItem;
		oldPosX = hudItem.posX;
		oldPosY = hudItem.posY;
	}

	@Override
	public void handleMouseInput() {
		int mouseX = Mouse.getEventX() * width / mc.displayWidth;
		int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
		hudItem.posX = mouseX - hudItem.getWidth() / 2;
		hudItem.posY = mouseY - hudItem.getHeight() / 2;
		

		if (axisAlign) {
			if (hudItem.posX > oldPosX - 5 && hudItem.posX < oldPosX + 5) {
				hudItem.posX = oldPosX;
			}
			if (hudItem.posY > oldPosY - 5 && hudItem.posY < oldPosY + 5) {
				hudItem.posY = oldPosY;
			}
		}
		hudItem.fixBounds();
		super.handleMouseInput();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		this.drawDefaultBackground();
		if (help) {
			drawCenteredString(
					mc.fontRenderer,
					StatCollector.translateToLocal("options.enviromine.hud.reposition"),
					width / 2, 16, 16777215);
			drawCenteredString(
					mc.fontRenderer,
					StatCollector.translateToLocal("options.enviromine.hud.alignment") +" "
							+ Alignment.calculateAlignment(mouseX, mouseY),
					width / 2, 26, 16777215);
		}

	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseState) {
		if (mouseState == 0) {
			hudItem.alignment = Alignment.calculateAlignment(mouseX, mouseY);
			mc.displayGuiScreen(parentScreen);
			SaveController.saveConfig(SaveController.UISettingsData);
		}
	}

	@Override
	public void handleKeyboardInput() {
		super.handleKeyboardInput();
		if (Keyboard.getEventKey() == Keyboard.KEY_LCONTROL || Keyboard.getEventKey() == Keyboard.KEY_RCONTROL) {
			axisAlign = Keyboard.getEventKeyState();
		}
	}

	@Override
	protected void keyTyped(char keyChar, int keyCode) {
		if (keyCode == Keyboard.KEY_ESCAPE) {
			hudItem.posX = oldPosX;
			hudItem.posY = oldPosY;
			mc.displayGuiScreen(parentScreen);
			SaveController.saveConfig(SaveController.UISettingsData);
		} else if (keyCode == Keyboard.KEY_R) {
			// hudItem.rotated = false;
			hudItem.posX = hudItem.getDefaultPosX();
			hudItem.posY = hudItem.getDefaultPosY();
			hudItem.alignment = hudItem.getDefaultAlignment();
			hudItem.fixBounds();
			mc.displayGuiScreen(parentScreen);
			SaveController.saveConfig(SaveController.UISettingsData);
		}
	}
}