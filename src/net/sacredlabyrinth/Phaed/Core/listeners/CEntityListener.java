package net.sacredlabyrinth.Phaed.Core.listeners;

import net.sacredlabyrinth.Phaed.Core.Core;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;

public class CEntityListener extends EntityListener
{
    private final Core plugin;

    /**
     * @param plugin
     */
    public CEntityListener(Core plugin)
    {
        this.plugin = plugin;
    }

    /**
     * @param event
     */
    @Override
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            Player player = (Player) event.getEntity();

            player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.ROTTEN_FLESH));
        }
    }
}
