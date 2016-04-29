package enviromine.client.gui.menu.update;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.client.gui.SaveController;
import enviromine.client.gui.UpdateNotification;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class NewsPage  extends GuiScreen{
	private GuiScreen parentGuiScreen;
	private PostGuiList newsPostList;
	public static int tabSelection;
	
	
	public NewsPage(GuiScreen par1GuiScreen, int tab)
	{
		this.parentGuiScreen = par1GuiScreen;
		tabSelection = tab;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		GuiButton newspage = new GuiButton(150, this.width/2 - 140, 4, 80, 20, StatCollector.translateToLocal("news.enviromine.button.news"));
		GuiButton versionpage = new GuiButton(151, this.width/2 - 40, 4, 80, 20, StatCollector.translateToLocal("news.enviromine.button.version"));
		GuiButton changelog = new GuiButton(152, this.width/2 + 60, 4, 80, 20, StatCollector.translateToLocal("news.enviromine.button.changelog"));
		
		switch(tabSelection)
		{
			case 150:
				newspage.enabled = false;
				break;
			case 151:
				versionpage.enabled = false;
				break;
			case 152:
				changelog.enabled = false;
				break;
		}
		
		this.buttonList.add(newspage);
		this.buttonList.add(versionpage);
		this.buttonList.add(changelog);
		
		this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height - 26, StatCollector.translateToLocal("gui.back")));

		this.newsPostList = new PostGuiList(this.mc, this.width, this.height, 32, this.height - 32, this.fontRendererObj.FONT_HEIGHT, tabSelection);
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return true;
	}
	
	/**
	 * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
	 */
	@Override
	public void actionPerformed(GuiButton par1GuiButton)
	{
		if (par1GuiButton.enabled)
		{
			if(par1GuiButton.id == 150)
			{
				this.mc.displayGuiScreen(new NewsPage(this.parentGuiScreen, 150));
			}
			else if(par1GuiButton.id == 151)
			{
				this.mc.displayGuiScreen(new NewsPage(this.parentGuiScreen, 151));

			}
			else if(par1GuiButton.id == 152)
			{
				this.mc.displayGuiScreen(new NewsPage(this.parentGuiScreen, 152));

			}
			else if (par1GuiButton.id == 200)
			{
				this.mc.displayGuiScreen(this.parentGuiScreen);
			}
		}
	}
	
	@Override
	protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_)
	{
		super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
		
	}
		
		
	@Override
	public void onGuiClosed()
	{
		UpdateNotification.updateLastSeen();
		SaveController.saveConfig(SaveController.UISettingsData);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj,  StatCollector.translateToLocal("options.enviromine.newsPage"), this.width / 2, 15, 16777215);
		
        this.newsPostList.drawScreen(par1, par2, par3);
		//drawNewsPost();
		
		super.drawScreen(par1, par2, par3);
	}
	

}
