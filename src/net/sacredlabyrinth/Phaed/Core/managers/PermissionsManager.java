package net.sacredlabyrinth.Phaed.Core.managers;

import net.sacredlabyrinth.Phaed.Core.Core;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class PermissionsManager
{
    public PermissionHandler permissions = null;
    private Core plugin;
    
    public PermissionsManager(Core plugin)
    {
	this.plugin = plugin;
	
	if (!startPermissions())
	{
	    Core.log.info("[" + plugin.getDescription().getName() + "] Permission system not found. Disabling plugin.");
	    plugin.getServer().getPluginManager().disablePlugin(plugin);
	}
    }
    
    public boolean hasPermission(Player player, String permission)
    {
	if (player == null)
	{
	    return true;
	}
	
	return (permissions != null && permissions.has(player, permission));
    }    
        
    @SuppressWarnings("static-access")
    public boolean startPermissions()
    {
	Plugin test = plugin.getServer().getPluginManager().getPlugin("Permissions");
	
	if (permissions == null)
	{
	    if (test != null)
	    {
		permissions = ((Permissions) test).getHandler();
		return true;
	    }
	}
	
	return false;
    }
}
