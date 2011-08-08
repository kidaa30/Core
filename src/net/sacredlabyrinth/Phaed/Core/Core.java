package net.sacredlabyrinth.Phaed.Core;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.sacredlabyrinth.Phaed.Core.managers.SettingsManager;
import net.sacredlabyrinth.Phaed.Core.managers.PermissionsManager;
import net.sacredlabyrinth.Phaed.Core.managers.CommandManager;
import net.sacredlabyrinth.Phaed.Core.managers.PlugManager;
import net.sacredlabyrinth.Phaed.Core.managers.ItemManager;
import net.sacredlabyrinth.Phaed.Core.managers.ItemManager.StackHolder;

import com.nilla.vanishnopickup.VanishNoPickup;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 * Core for Bukkit
 * 
 * @author Phaed
 */
public class Core extends JavaPlugin
{
    public SettingsManager settings;
    public PermissionsManager pm;
    public CommandManager cm;
    public PlugManager plm;
    public ItemManager im;

    //Nilla added this
    public VanishNoPickup vanishPlugin;
    
    public static Logger log;
    
    @Override
    public void onEnable()
    {
	settings = new SettingsManager(this);
	pm = new PermissionsManager(this);
	cm = new CommandManager(this);
	plm = new PlugManager(this);
	im = new ItemManager();
	log = Logger.getLogger("Minecraft");
        setupVanish();
	
	log.info("[" + this.getDescription().getName() + "] version [" + this.getDescription().getVersion() + "] loaded");
    }
    
    @Override
    public void onDisable()
    {
	
    }
    private void setupVanish(){
        PluginDescriptionFile pdfFile = this.getDescription();
        Plugin this_plugin = this.getServer().getPluginManager().getPlugin("VanishNoPickup");

        if(vanishPlugin == null) {
            if(this_plugin != null) {
                vanishPlugin = ((VanishNoPickup)this_plugin);
                log.info("[" + this.getDescription().getName() + "] has VanishNoPickup Plugin support");
            }else{
                log.info("[" + this.getDescription().getName() + "] Failed to find VanishNoPickup Plugin");
            } 
        }

    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
    {
	try
	{
	    String[] split = args;
	    String commandName = command.getName().toLowerCase();
	    
	    if (commandName.equals("time"))
	    {
		if (sender instanceof Player)
		{
		    Player player = (Player) sender;
		    
		    if (pm.hasPermission(player, "core.time"))
		    {
			if (split.length > 0)
			{
			    String time = split[0];
			    
			    if (cm.time(player, time))
			    {
				Core.log.info("[core] " + player.getName() + " changed time to " + time);
				return true;
			    }
			}
			
		    }
		    player.sendMessage(ChatColor.RED + "Usage: /time [day|night|dusk|dawn]");
		}
		else
		{
		    sender.sendMessage("Command requires a player");
		}
	    }
	    else if ((commandName.equals("i") || commandName.equals("item")))
	    {
		if (sender instanceof Player)
		{
		    Player player = (Player) sender;
		    
		    if (pm.hasPermission(player, "core.item"))
		    {
			if (split.length > 0)
			{
			    String item = split[0];
			    String count = split.length > 1 ? split[1] : null;
			    
			    ArrayList<StackHolder> stacks = im.getStacks(player, item, count);
			    
			    for (StackHolder stack : stacks)
			    {
				im.PutStackInHand(player, stack.getStack());
				Core.log.info("[core] " + player.getName() + " spawned " + stack.getCount() + " " + Helper.friendlyBlockType(stack.getStack().getType().toString()));
				ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "You got " + stack.getCount() + " " + Helper.friendlyBlockType(stack.getStack().getType().toString()));
			    }
			    return true;
			}
		    }
		    player.sendMessage(ChatColor.RED + "Usage: /item <id|id-range|name> [count]");
		}
		else
		{
		    sender.sendMessage("Command requires a player");
		}
	    }
	    else if ((commandName.equals("ims") || commandName.equals("items")))
	    {
		if (sender instanceof Player)
		{
		    Player player = (Player) sender;
		    
		    if (pm.hasPermission(player, "core.items"))
		    {
			if (split.length > 0)
			{
			    ArrayList<String> items = new ArrayList<String>();
			    
			    for (int i = 0; i < split.length; i++)
			    {
				items.add(split[i]);
			    }
			    
			    for (String mitem : items)
			    {
				ArrayList<StackHolder> stacks = im.getStacks(player, mitem, null);
				
				for (StackHolder stack : stacks)
				{
				    im.PutStackInHand(player, stack.getStack());
				    Core.log.info("[core] " + player.getName() + " spawned " + stack.getCount() + " " + Helper.friendlyBlockType(stack.getStack().getType().toString()));
				    ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "You got " + stack.getCount() + " " + Helper.friendlyBlockType(stack.getStack().getType().toString()));
				}
			    }
			    return true;
			}
		    }
		    player.sendMessage(ChatColor.RED + "Usage: /items [id|id-range|name] [id|id-range|name] ...");
		}
		else
		{
		    sender.sendMessage("Command requires a player");
		}
	    }
	    else if (commandName.equals("give"))
	    {
		if (sender instanceof Player)
		{
		    Player player = (Player) sender;
		    
		    if (pm.hasPermission(player, "core.give"))
		    {
			if (split.length > 1)
			{
			    Player receiver = Helper.matchUniquePlayer(this, split[0]);
			    
			    if (receiver != null)
			    {
				String item = split[1];
				String count = split.length > 2 ? split[2] : null;
				
				ArrayList<StackHolder> stacks = im.getStacks(player, item, count);
				
				for (StackHolder stack : stacks)
				{
				    im.PutStackInHand(receiver, stack.getStack());
				    Core.log.info("[core] " + player.getName() + " has given " + receiver.getName() + " " + stack.getCount() + " " + Helper.friendlyBlockType(stack.getStack().getType().toString()));
				    ChatBlock.sendMessage(receiver, ChatColor.LIGHT_PURPLE + player.getName() + " has given you " + stack.getCount() + " " + Helper.friendlyBlockType(stack.getStack().getType().toString()));
				    ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "You have given " + receiver.getName() + " " + stack.getCount() + " " + Helper.friendlyBlockType(stack.getStack().getType().toString()));
				}
				return true;
			    }
			    
			    player.sendMessage(ChatColor.RED + "Could not find player " + split[0]);
			    return true;
			}
		    }
		    player.sendMessage(ChatColor.RED + "Usage: /give [player] [id|id-range|name] [count] ...");
		}
		else
		{
		    sender.sendMessage("Command requires a player");
		}
	    }
	    else if (commandName.equals("who") || commandName.equals("list"))
	    {
		if (sender instanceof Player)
		{
		    Player player = (Player) sender;
		    
		    if (pm.hasPermission(player, "core.who"))
		    {
			cm.who(player, player.getWorld().getName());
		    }
		    
		    return true;
		}
		
		if (split.length > 0)
		{
		    String world = split[0];		    
		    cm.who(sender, world);
		}
		else
		{		    
		    cm.who(sender, getServer().getWorlds().get(0).getName());
		}
		
		return true;
	    }
	    else if (commandName.equals("msg"))
	    {
		if (sender instanceof Player)
		{
		    Player player = (Player) sender;
		    
		    if (pm.hasPermission(player, "core.msg"))
		    {
			if (split.length > 1)
			{
			    String to = split[0];
			    
			    String msg = "";
			    
			    for (int i = 1; i < split.length; i++)
			    {
				msg += split[i] + " ";
			    }
			    
			    msg = msg.trim();
			    
			    if (!cm.msg(player, to, msg))
			    {
				player.sendMessage(ChatColor.RED + "There are no players matching that name");
			    }
			    return true;
			}
		    }
		    player.sendMessage(ChatColor.RED + "Usage: /msg [player] [message]");
		}
		else
		{
		    sender.sendMessage("Command requires a player");
		}
	    }
	    else if (commandName.equals("m"))
	    {
		if (sender instanceof Player)
		{
		    Player player = (Player) sender;
		    
		    if (pm.hasPermission(player, "core.msg"))
		    {
			if (split.length > 0)
			{
			    String msg = "";
			    
			    for (int i = 0; i < split.length; i++)
			    {
				msg += split[i] + " ";
			    }
			    
			    msg = msg.trim();
			    
			    if (!cm.m(player, msg))
			    {
				player.sendMessage(ChatColor.RED + "You are not in a conversation");
			    }
			    return true;
			}
		    }
		    player.sendMessage(ChatColor.RED + "Usage: /m [message]");
		}
		else
		{
		    sender.sendMessage("Command requires a player");
		}
	    }
	    else if (commandName.equals("clear"))
	    {
		if (sender instanceof Player)
		{
		    Player player = (Player) sender;
		    
		    if (pm.hasPermission(player, "core.clear"))
		    {
			player.getInventory().clear();
			ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "Inventory cleared");
			return true;
		    }
		}
		else
		{
		    sender.sendMessage("Command requires a player");
		}
	    }
	    else if (commandName.equals("plugin"))
	    {
		if (sender instanceof Player)
		{
		    Player player = (Player) sender;
		    
		    if (!pm.hasPermission(player, "core.plugin"))
		    {
			return false;
		    }
		}
		
		if (split.length > 0)
		{
		    String cmd = split[0];
		    
		    if (split.length > 1)
		    {
			String plug = split[1];
			
			if (cmd.equals("load"))
			{
			    plm.loadPlugin(plug, sender);
			    return true;
			}
			if (cmd.equals("reload"))
			{
			    plm.reloadPlugin(plug, sender);
			    return true;
			}
			if (cmd.equals("enable"))
			{
			    plm.enablePlugin(plug, sender);
			    return true;
			}
			if (cmd.equals("disable"))
			{
			    plm.disablePlugin(plug, sender);
			    return true;
			}
		    }
		    
		    if (cmd.equals("list"))
		    {
			plm.listPlugins(sender);
			return true;
		    }
		}
		
		sender.sendMessage(ChatColor.RED + "Usage: /plugin [load|reload|enable|disable|list] [plugin]");
	    }
	    else if (commandName.equals("coords"))
	    {
	    	if(sender instanceof Player)
			{
				Player plr = (Player) sender;
				Location plrloc = plr.getLocation();
				plr.sendMessage(ChatColor.RED + "You are at X: " + plrloc.getBlockX() + " Y: " + plrloc.getBlockY() + " Z: " + plrloc.getBlockZ());
			}
	    	else
	    	{
	    		sender.sendMessage("Command requires a player");
	    	}
	    	return true;
	    }
	    return false;
	}
	catch (Throwable ex)
	{
	    ex.printStackTrace();
	    return true;
	}
    }
}
