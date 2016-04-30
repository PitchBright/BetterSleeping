package foxie.bettersleeping;

import java.lang.reflect.Field;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cz.ondraster.bettersleeping.api.PlayerData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

public class BSCommand extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "bs";
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "bs [set/add/sub] #: Sets or adds the sleep value";
	}

	private Field m_worldTime;
	
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if(args.length != 2)
		{
			
			sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
			sender.addChatMessage(new ChatComponentText("Command needs two arguments!"));
			
			return;
		}
		
		long number = 0;
		
		try
		{
			number = Long.parseLong(args[1]);
		} catch(NumberFormatException e)
		{
			sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
			sender.addChatMessage(new ChatComponentText("The second argument needs to be a valid integral number!"));
		}
		
		if(!(sender instanceof EntityPlayer))
		{
			sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
			sender.addChatMessage(new ChatComponentText("Command has to be send from a player!"));
			
			return;
		}
		
		PlayerData data = BSSavedData.instance().getData((EntityPlayer) sender);
		
		if(args[0].equals("set"))
		{
			data.setSleepLevel(number);
		}
		else if(args[0].equals("add"))
		{
			data.increaseSleepLevel(number);
		}
		else if(args[0].equals("sub"))
		{
			data.decreaseSleepLevel(number);
		}
		else if(args[0].equals("addworld"))
		{
			getWorld().getWorldInfo().incrementTotalWorldTime(number);
			getWorld().setWorldTime((getWorld().getWorldTime() + number) % 24000);
			
			sender.addChatMessage(new ChatComponentText("Total world time is now " + getWorld().getTotalWorldTime()));
		}
		else if(args[0].equals("setworld"))
		{
			if(m_worldTime == null)
			{
				m_worldTime = ReflectionHelper.findField(WorldInfo.class, "totalTime");
				m_worldTime.setAccessible(true);
			}
			
			try
			{
				m_worldTime.set(getWorld().getWorldInfo(), number);
				getWorld().setWorldTime(number % 24000);
			} catch(Exception e)
			{
				e.printStackTrace();
			}
			
			sender.addChatMessage(new ChatComponentText("Total world time is now " + getWorld().getTotalWorldTime()));
		}
		
		sender.addChatMessage(new ChatComponentText("Sleep level is now " + data.getSleepLevel()));
	}
	
	private World getWorld()
	{
		return MinecraftServer.getServer().getEntityWorld();
	}

}
