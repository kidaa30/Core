package net.sacredlabyrinth.Phaed.Core.listeners;

import net.sacredlabyrinth.Phaed.Core.Core;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPreLoginEvent;

/**
 * PreciousStones player listener
 *
 * @author Phaed
 */
public class CPlayerListener extends PlayerListener
{
    private final Core plugin;

    /**
     *
     * @param plugin
     */
    public CPlayerListener(Core plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void onPlayerPreLogin(PlayerPreLoginEvent event)
    {
        if (plugin.settings.lockDown)
        {
            event.setKickMessage(plugin.settings.lockDownMsg);
            String group = plugin.pm.permissions.getGroup("world", event.getName());

            if (group.equals("Default"))
            {
                event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, plugin.settings.lockDownMsg);
                Core.log.info("Player " + event.getName() + " was locked out");
            }
        }
    }
}
