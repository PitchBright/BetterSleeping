package enviromine.core.commands;

import enviromine.core.EM_Settings;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;

public class CommandPhysics extends CommandBase
{

	private String on = StatCollector.translateToLocal("options.enviromine.on");
	private String off = StatCollector.translateToLocal("options.enviromine.off");
	private String toggle = StatCollector.translateToLocal("commands.enviromine.envirophysic.toggle");
	private String status = StatCollector.translateToLocal("commands.enviromine.envirophysic.status");

	@Override
	public String getCommandName() 
	{
		return "envirophysic";
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}
	
	@Override
	public String getCommandUsage(ICommandSender icommandsender) 
	{
		return "/envirophysic <"+on+", "+off+", "+toggle+", "+status+">";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] astring) 
	{
		
		if(astring.length != 1)
		{
			this.ShowUsage(sender);
			return;
		}
		try
		{
			
			if(astring[0].equalsIgnoreCase(toggle))
			{
				this.togglePhy(sender);
			}
			else if(astring[0].equalsIgnoreCase(on))
			{
				doPhy(true , sender);
			}
			else if(astring[0].equalsIgnoreCase(off))
			{
				doPhy(false , sender);
			}
			else if(astring[0].equalsIgnoreCase(status))
			{
				String Status = "";
				if(EM_Settings.enablePhysics)
				{
					Status = on;
				}
				else
				{
					Status = off;
				}
				sender.addChatMessage(new ChatComponentTranslation("commands.enviromine.envirophysic.statusText", "Enviromine", Status));
			}
			else
			{
				this.ShowUsage(sender);
				return;
			}
			
		} catch(Exception e)
		{
			this.ShowUsage(sender);
			return;
		}
	}
	
	
	public void ShowUsage(ICommandSender sender)
	{
		sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
	}
	
	public void togglePhy( ICommandSender sender)
	{
		
		EM_Settings.enablePhysics = !EM_Settings.enablePhysics;
		
		if(EM_Settings.enablePhysics)
		{
			sender.addChatMessage(new ChatComponentTranslation("commands.enviromine.envirophysic.physics", "Enviromine", "On"));
		}
		else
		{
			sender.addChatMessage(new ChatComponentTranslation("commands.enviromine.envirophysic.physics", "Enviromine", "Off"));
		}
	}
	
	public void doPhy(boolean what, ICommandSender sender)
	{
		EM_Settings.enablePhysics = what;
		
		if(what)
		{
			sender.addChatMessage(new ChatComponentTranslation("commands.enviromine.envirophysic.physics","Enviromine", "On"));
		}
		else
		{
			sender.addChatMessage(new ChatComponentTranslation("commands.enviromine.envirophysic.physics","Enviromine", "Off"));
		}
	}
}
