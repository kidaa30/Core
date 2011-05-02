package net.sacredlabyrinth.Phaed.Core.managers;

import net.sacredlabyrinth.Phaed.Core.Helper;
import net.sacredlabyrinth.Phaed.Core.Core;
import net.sacredlabyrinth.Phaed.Core.ChatBlock;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

public class CommandManager extends PlayerListener
{
    private final Core plugin;
    private final HashMap<String, String> conversations = new HashMap<String, String>();
    
    public CommandManager(Core plugin)
    {
	this.plugin = plugin;
    }
    
    public boolean time(Player player, String time)
    {
	long curtime = player.getWorld().getTime();
	long newtime = curtime - (curtime % 24000);
	
	if (time.equalsIgnoreCase("day"))
	{
	    newtime += 0;
	}
	else if (time.equalsIgnoreCase("night"))
	{
	    newtime += 14000;
	}
	else if (time.equalsIgnoreCase("dusk"))
	{
	    newtime += 12500;
	}
	else if (time.equalsIgnoreCase("dawn"))
	{
	    newtime += 23000;
	}
	else
	{
	    return false;
	}
	
	player.getWorld().setTime(newtime);
	player.sendMessage(ChatColor.LIGHT_PURPLE + "It is now " + time);
	return true;
    }
    
    public void who(CommandSender sender, String world)
    {
	HashMap<String, HashSet<Player>> groups = new HashMap<String, HashSet<Player>>();
	String playerList = "";

	// sort players into groups
	
	Player[] online = plugin.getServer().getOnlinePlayers();
	
	for (int i = 0; i < online.length; i++)
	{	    
	    String group = plugin.pm.permissions.getGroup(world, online[i].getName());
	    
	    if (groups.containsKey(group))
	    {
		groups.get(group).add(online[i]);
	    }
	    else
	    {
		HashSet<Player> players = new HashSet<Player>();
		players.add(online[i]);
		groups.put(group, players);
	    }
	}
	
	// build the final list with the player's prefix and suffix
	
	for (String g : groups.keySet())
	{
	    for (Player pl : groups.get(g))
	    {
		String prefix = plugin.pm.permissions.getGroupPrefix(world, g).replace("&", "§");
		String suffix = plugin.pm.permissions.getGroupSuffix(world, g).replace("&", "§");
		
		playerList += ChatColor.DARK_GRAY + ", " + prefix + pl.getName() + suffix;
	    }
	}
	
	if (playerList.length() == 0)
	{
	    playerList = "empty";
	}
	else
	{
	    playerList = playerList.substring(4);
	}
	
	ChatBlock.sendMessage(sender, ChatColor.WHITE + "Who's online " + ChatColor.GRAY + "(" + plugin.getServer().getOnlinePlayers().length + "/" + plugin.getServer().getMaxPlayers() + "): " + playerList);
    }
    
    public boolean msg(Player player, String to, String msg)
    {
	Player toplayer = Helper.matchUniquePlayer(plugin, to);
	
	if (toplayer == null)
	{
	    return false;
	}
	
	if ((conversations.containsKey(player.getName()) && conversations.get(player.getName()).equals(to)) || (conversations.containsKey(to) && conversations.get(to).equals(player.getName())))
	{
	    ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.DARK_GRAY + "(" + ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.LIGHT_PURPLE + toplayer.getName() + ChatColor.DARK_GRAY + ") " + ChatColor.BLUE + msg);
	    ChatBlock.sendMessage(toplayer, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.DARK_GRAY + "(" + ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.LIGHT_PURPLE + toplayer.getName() + ChatColor.DARK_GRAY + ") " + ChatColor.BLUE + msg);
	    
	    Core.log.info("[msg] (" + player.getName() + ">" + toplayer.getName() + ") " + msg);
	    return true;
	}
	
	if (conversations.containsKey(player.getName()))
	{
	    ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.RED + "Ended your conversation with " + conversations.get(player.getName()));
	    
	    Player playerother = Helper.matchUniquePlayer(plugin, conversations.get(player.getName()));
	    
	    if (playerother != null)
	    {
		ChatBlock.sendMessage(playerother, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.RED + Helper.capitalize(player.getName()) + " ended his conversation with you.");
	    }
	}
	
	ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.LIGHT_PURPLE + "Started a conversation. Reply with /m.");
	ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.DARK_GRAY + "(" + ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.LIGHT_PURPLE + toplayer.getName() + ChatColor.DARK_GRAY + ") " + ChatColor.BLUE + msg);
	
	if (conversations.containsKey(toplayer.getName()))
	{
	    ChatBlock.sendMessage(toplayer, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.RED + "Ended your conversation with " + conversations.get(toplayer.getName()));
	    
	    Player playerother = Helper.matchUniquePlayer(plugin, conversations.get(toplayer.getName()));
	    
	    if (playerother != null)
	    {
		ChatBlock.sendMessage(playerother, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.RED + Helper.capitalize(toplayer.getName()) + " ended his conversation with you.");
	    }
	}
	
	ChatBlock.sendMessage(toplayer, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.LIGHT_PURPLE + "A conversation was started with you. Reply with /m.");
	ChatBlock.sendMessage(toplayer, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.DARK_GRAY + "(" + ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.LIGHT_PURPLE + toplayer.getName() + ChatColor.DARK_GRAY + ") " + ChatColor.BLUE + msg);
	
	Core.log.info("[msg] (" + player.getName() + ">" + toplayer.getName() + ") " + msg);
	
	conversations.put(player.getName(), toplayer.getName());
	conversations.put(toplayer.getName(), player.getName());
	
	return true;
    }
    
    public boolean m(Player player, String msg)
    {
	if (!conversations.containsKey(player.getName()))
	{
	    return false;
	}
	
	Player toplayer = Helper.matchUniquePlayer(plugin, conversations.get(player.getName()));
	
	if (toplayer == null)
	{
	    return false;
	}
	
	ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.DARK_GRAY + "(" + ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.LIGHT_PURPLE + toplayer.getName() + ChatColor.DARK_GRAY + ") " + ChatColor.BLUE + msg);
	ChatBlock.sendMessage(toplayer, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.DARK_GRAY + "(" + ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.LIGHT_PURPLE + toplayer.getName() + ChatColor.DARK_GRAY + ") " + ChatColor.BLUE + msg);
	
	Core.log.info("[msg] (" + player.getName() + ">" + toplayer.getName() + ") " + msg);
	
	conversations.put(player.getName(), toplayer.getName());
	return true;
    }
}
