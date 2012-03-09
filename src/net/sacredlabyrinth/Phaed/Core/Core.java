package net.sacredlabyrinth.Phaed.Core;


import com.platymuus.bukkit.permissions.PermissionsPlugin;
import in.mDev.MiracleM4n.mChatSuite.mChatSuite;
import net.sacredlabyrinth.Phaed.Core.listeners.CoreEventListener;
import net.sacredlabyrinth.Phaed.Core.managers.CommandManager;
import net.sacredlabyrinth.Phaed.Core.managers.ItemManager;
import net.sacredlabyrinth.Phaed.Core.managers.PlugManager;
import net.sacredlabyrinth.Phaed.Core.managers.SettingsManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.kitteh.vanish.staticaccess.VanishNoPacket;

/**
 * Core for Bukkit
 *
 * @author Phaed
 */
public class Core extends JavaPlugin
{
    private CoreEventListener eventListener;

    private SettingsManager settings;
    private CommandManager cm;
    private PlugManager plm;
    private ItemManager im;
    public static Logger log;
    public VanishNoPacket vanishPlugin;
    private PermissionsPlugin perms;
    public mChatSuite mchatSuite;
    private int[] throughFields = new int[]{0};

    @Override
    public void onEnable()
    {
        eventListener = new CoreEventListener(this);
        settings = new SettingsManager(this);
        plm = new PlugManager(this);
        im = new ItemManager();
        cm = new CommandManager(this);
        log = Logger.getLogger("Minecraft");

        setupVanish();
        setupPermissionsBukkit();
        setupMChatSuite();

        getCommand("setrank").setExecutor(cm);
        getCommand("setvip").setExecutor(cm);
        getCommand("lockdown").setExecutor(cm);
        getCommand("data").setExecutor(cm);
        getCommand("sun").setExecutor(cm);
        getCommand("storm").setExecutor(cm);
        getCommand("day").setExecutor(cm);
        getCommand("night").setExecutor(cm);
        getCommand("i").setExecutor(cm);
        getCommand("item").setExecutor(cm);
        getCommand("ims").setExecutor(cm);
        getCommand("items").setExecutor(cm);
        getCommand("give").setExecutor(cm);
        getCommand("who").setExecutor(cm);
        getCommand("maxxp").setExecutor(cm);
        getCommand("list").setExecutor(cm);
        getCommand("clear").setExecutor(cm);
        getCommand("msg").setExecutor(cm);
        getCommand("m").setExecutor(cm);
        getCommand("plugin").setExecutor(cm);
        getCommand("coords").setExecutor(cm);

        getServer().getPluginManager().registerEvents(eventListener, this);

        log.setFilter(new Filter()
        {
            public boolean isLoggable(LogRecord record)
            {
                if (record.getMessage().contains("overloaded?"))
                {
                    return false;
                }

                if (record.getMessage().contains("Unsupported operation"))
                {
                    return false;
                }

                if (record.getMessage().contains("You moved too quickly"))
                {
                    return false;
                }

                if (record.getMessage().contains("Attempted to place a tile entity where there was no entity tile!"))
                {
                    return false;
                }

                return (record.getMessage() == null) || (record.getLevel() != Level.WARNING);
            }
        });

        log.info("[" + this.getDescription().getName() + "] version [" + this.getDescription().getVersion() + "] loaded");
    }

    private void setupVanish()
    {
        Plugin this_plugin = getServer().getPluginManager().getPlugin("VanishNoPacket");

        if (vanishPlugin == null)
        {
            if (this_plugin != null)
            {
                vanishPlugin = ((VanishNoPacket) this_plugin);
            }
            else
            {
                log.info("[" + getDescription().getName() + "] Failed to find VanishNoPacket");
            }
        }
    }

    /*
    * Fake main to allow us to run from netbeans
    */
    public static void main(String[] args){  }

    private void setupPermissionsBukkit()
    {
        Plugin plug = getServer().getPluginManager().getPlugin("PermissionsBukkit");

        if (perms == null)
        {
            if (plug != null)
            {
                perms = ((PermissionsPlugin) plug);
            }
            else
            {
                log.info("[" + getDescription().getName() + "] Failed to find PermissionsBukkit");
            }
        }
    }

    private void setupMChatSuite()
    {
        Plugin plug = getServer().getPluginManager().getPlugin("mChatSuite");

        if (mchatSuite == null)
        {
            if (plug != null)
            {
                mchatSuite = ((mChatSuite) plug);
            }
            else
            {
                log.info("[" + getDescription().getName() + "] Failed to find mChatSuite");
            }
        }
    }

    public void onDisable()
    {
    }

    public SettingsManager getSettings()
    {
        return settings;
    }

    public PermissionsPlugin getPerms()
    {
        return perms;
    }

    public CommandManager getCm()
    {
        return cm;
    }

    public PlugManager getPlm()
    {
        return plm;
    }

    public ItemManager getIm()
    {
        return im;
    }

    public int[] getThroughFields()
    {
        return throughFields;
    }
}
