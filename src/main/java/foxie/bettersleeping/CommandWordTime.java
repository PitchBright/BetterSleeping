package foxie.bettersleeping;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

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
		return "worldtime [set/add] #: Sets or adds to the total world time!";
	}

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
			getWorld().getWorldInfo().incrementTotalWorldTime(getWorld().getTotalWorldTime() + number);
			getWorld().setWorldTime((getWorld().getWorldTime() + number) % 24000);
		}
		else if(args[0].equals("set"))
		{
			getWorld().getWorldInfo().incrementTotalWorldTime(number);
			;
			getWorld().setWorldTime(number % 24000);

		}

		sender.addChatMessage(new ChatComponentText("Total world time is now " + getWorld().getTotalWorldTime()));
	}

	private World getWorld()
	{
		return MinecraftServer.getServer().getEntityWorld();
	}

}
