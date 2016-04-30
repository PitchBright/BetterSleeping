package foxie.bettersleeping;

import java.lang.reflect.Field;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

public class CommandWordTime extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "worldtime";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_)
	{
		return null;
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

		if(args[0].equals("add"))
		{
			getWorld().getWorldInfo().incrementTotalWorldTime(number);
			getWorld().setWorldTime((getWorld().getWorldTime() + number) % 24000);
		}
		else if(args[0].equals("set"))
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

		}

		sender.addChatMessage(new ChatComponentText("Total world time is now " + getWorld().getTotalWorldTime()));
	}

	private World getWorld()
	{
		return MinecraftServer.getServer().getEntityWorld();
	}

}
