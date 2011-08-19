package net.sacredlabyrinth.Phaed.Core.listeners;

import com.platymuus.bukkit.permissions.Group;
import java.util.List;
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

            List<Group> gs = plugin.perms.getGroups(event.getName());

            if (gs.size() == 1)
            {
                if (gs.get(0).getName().equalsIgnoreCase("default"))
                {
                    event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, plugin.settings.lockDownMsg);
                    Core.log.info("Player " + event.getName() + " was locked out");
                }
            }
        }

        if (plugin.settings.lockUp)
        {
            event.setKickMessage(plugin.settings.lockDownMsg);

            List<Group> gs = plugin.perms.getGroups(event.getName());

            if (gs.size() == 1)
            {
                if (gs.get(0).getName().equalsIgnoreCase("default"))
                {
                    event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, plugin.settings.lockDownMsg);
                    Core.log.info("Player " + event.getName() + " was locked out");
                }
            }
        }
    }
}
