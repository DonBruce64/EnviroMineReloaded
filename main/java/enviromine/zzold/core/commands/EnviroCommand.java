package enviromine.core.commands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import enviromine.handlers.EM_StatusManager;
import enviromine.trackers.EnviroDataTracker;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

public class EnviroCommand extends CommandBase
{
	
	private String add = I18n.format("commands.enviromine.envirostat.add");
	private String set = I18n.format("commands.enviromine.envirostat.set");
	private String temp = I18n.format("commands.enviromine.envirostat.temp");
	private String sanity = I18n.format("commands.enviromine.envirostat.sanity");
	private String water = I18n.format("commands.enviromine.envirostat.water");
	private String air = I18n.format("commands.enviromine.envirostat.air");
	
	@Override
	public String getCommandName()
	{
		return "envirostat";
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/envirostat <playername> <"+add+", "+set+"> <"+temp+", "+sanity+", "+water+", "+air+"> <float>";
	}
	
	@Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] astring)
	{

		if(astring.length != 4)
		{
			this.ShowUsage(sender);
			return;
		}
		
		EntityPlayerMP player;
		try{
			player = getPlayer(server, sender, astring[0]);
		}catch (PlayerNotFoundException e1){
			e1.printStackTrace();
			return;
		}
		
		String target = player.getName();
		
		EnviroDataTracker tracker = EM_StatusManager.lookupTrackerFromUsername(target);
		
		if(tracker == null)
		{
			this.ShowNoTracker(sender);
			return;
		}
		
		try
		{
			float value = Float.parseFloat(astring[3]);
			
			if(astring[1].equalsIgnoreCase(add))
			{
				if(astring[2].equalsIgnoreCase(temp))
				{
					tracker.bodyTemp += value;
				} else if(astring[2].equalsIgnoreCase(sanity))
				{
					tracker.sanity += value;
				} else if(astring[2].equalsIgnoreCase(water))
				{
					tracker.hydration += value;
				} else if(astring[2].equalsIgnoreCase(air))
				{
					tracker.airQuality += value;
				} else
				{
					this.ShowUsage(sender);
					return;
				}
			} else if(astring[1].equalsIgnoreCase(set))
			{
				if(astring[2].equalsIgnoreCase(temp))
				{
					tracker.bodyTemp = value;
				} else if(astring[2].equalsIgnoreCase(sanity))
				{
					tracker.sanity = value;
				} else if(astring[2].equalsIgnoreCase(water))
				{
					tracker.hydration = value;
				} else if(astring[2].equalsIgnoreCase("air"))
				{
					tracker.airQuality = value;
				} else
				{
					this.ShowUsage(sender);
					return;
				}
			} else
			{
				this.ShowUsage(sender);
				return;
			}
			
			tracker.fixFloatinfPointErrors();
			return;
		} catch(Exception e)
		{
			this.ShowUsage(sender);
			return;
		}
	}
	
	public void ShowUsage(ICommandSender sender)
	{
		sender.addChatMessage(new TextComponentTranslation(getCommandUsage(sender)));
	}
	
	public void ShowNoTracker(ICommandSender sender)
	{
		sender.addChatMessage(new TextComponentTranslation("commands.enviromine.envirostat.noTracker"));
	}

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
	@SuppressWarnings("unchecked")
	@Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] strings, @Nullable BlockPos pos)
    {
        if(strings.length == 1)
        {
        	return getListOfStringsMatchingLastWord(strings, server.getAllUsernames());
        } else if(strings.length == 2)
        {
        	return getListOfStringsMatchingLastWord(strings, new String[]{add, set});
        } else if(strings.length == 3)
        {
        	return getListOfStringsMatchingLastWord(strings, new String[]{temp, sanity, water, air});
        } else
        {
        	return new ArrayList<String>();
        }
    }
}
