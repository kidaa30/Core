package net.sacredlabyrinth.Phaed.Core.managers;

import net.sacredlabyrinth.Phaed.Core.Core;
import org.bukkit.util.config.Configuration;

public class SettingsManager
{
    public boolean disableBypassAlertsForAdmins;

    private Core plugin;
    
    public SettingsManager(Core plugin)
    {
	this.plugin = plugin;
	
	loadConfiguration();
    }
    
    /**
     * Load the configuration
     */
    public void loadConfiguration()
    {
	Configuration config = plugin.getConfiguration();
	config.load();	
	
	disableBypassAlertsForAdmins = config.getBoolean("settings.disable-bypass-alerts-for-admins", false);
    }
}