package net.sacredlabyrinth.Phaed.Core.managers;

import net.sacredlabyrinth.Phaed.Core.Core;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class SettingsManager
{
    public boolean disableBypassAlertsForAdmins;
    public boolean lockDown;
    public boolean lockUp;
    public String lockDownMsg = "Server is temporarily locked down.  Only members can enter";
    private File main;
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
        FileConfiguration config = plugin.getConfig();
        main = new File(plugin.getDataFolder() + File.separator + "config.yml");
        boolean exists = (main).exists();

        if (exists)
        {
            try
            {
                config.options().copyDefaults(true);
                config.load(main);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            config.options().copyDefaults(true);
        }

        disableBypassAlertsForAdmins = config.getBoolean("settings.disable-bypass-alerts-for-admins", false);
    }
}