package net.sacredlabyrinth.Phaed.Core.managers;

import net.sacredlabyrinth.Phaed.Core.Core;
import org.bukkit.util.config.Configuration;

public class SettingsManager
{
    public boolean disableBypassAlertsForAdmins;
    public boolean lockDown;
    public String lockDownMsg = "Server is temporarily locked down.  Only members can enter";

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