package net.sacredlabyrinth.Phaed.Core.managers;

import java.io.File;
import net.sacredlabyrinth.Phaed.Core.Core;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class PlugManager
{
    private Core plugin;

    public PlugManager(Core plugin)
    {
	this.plugin = plugin;
    }

    public void listPlugins(CommandSender player)
    {
	StringBuilder plugins = new StringBuilder();
	for (Plugin p : plugin.getServer().getPluginManager().getPlugins())
	{
	    plugins.append(p.isEnabled() ? " " + ChatColor.GREEN : " " + ChatColor.RED);
	    plugins.append(p.getDescription().getName());
	}

	plugins.insert(0, ChatColor.YELLOW + "Plugins:" + ChatColor.WHITE);
	player.sendMessage(plugins.toString());
    }

    public boolean reloadPlugin(String name, CommandSender player)
    {
	return disablePlugin(name, player) && enablePlugin(name, player);
    }

    public boolean loadPlugin(String name, CommandSender sender)
    {
	try
	{
	    PluginManager pm = plugin.getServer().getPluginManager();
	    pm.loadPlugin(new File("plugins", name + ".jar"));
	    sender.sendMessage(ChatColor.YELLOW + "Plugin loaded.");
	    return enablePlugin(name, sender);
	}
	catch (Throwable ex)
	{
	    sender.sendMessage(ChatColor.RED + "Could not load plugin. Is the file named properly?");
	    return false;
	}
    }

    public boolean enablePlugin(String name, CommandSender sender)
    {
	try
	{
	    final PluginManager pm = plugin.getServer().getPluginManager();
	    final Plugin plugin = pm.getPlugin(name);

	    if(plugin == null)
	    {
		sender.sendMessage(ChatColor.RED + "Plugin not found.");
		return false;
	    }

	    if (!plugin.isEnabled())
		new Thread(new Runnable()
		{
		    public void run()
		    {
			synchronized (pm)
			{
			    pm.enablePlugin(plugin);
			}
		    }
		}).start();
	    sender.sendMessage(ChatColor.YELLOW + "Plugin enabled.");
	    return true;
	}
	catch (Throwable ex)
	{
	    sender.sendMessage(ex.getMessage());
	    return false;
	}
    }

    public boolean disablePlugin(String name, CommandSender sender)
    {
	try
	{
	    final PluginManager pm = plugin.getServer().getPluginManager();
	    final Plugin plugin = pm.getPlugin(name);

	    if(plugin == null)
	    {
		sender.sendMessage(ChatColor.RED + "Plugin not found.");
		return false;
	    }

	    if (plugin.isEnabled())
		new Thread(new Runnable()
		{
		    public void run()
		    {
			synchronized (pm)
			{
			    pm.disablePlugin(plugin);
			}
		    }
		}).start();
	    sender.sendMessage(ChatColor.YELLOW + "Plugin disabled.");
	    return true;
	}
	catch (Throwable ex)
	{
	    sender.sendMessage(ex.getMessage());
	    return false;
	}
    }
}
