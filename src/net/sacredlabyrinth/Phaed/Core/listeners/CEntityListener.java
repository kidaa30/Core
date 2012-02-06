package net.sacredlabyrinth.Phaed.Core.listeners;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.server.SpawnerCreature;
import net.sacredlabyrinth.Phaed.Core.Core;
import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
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

    @Override
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        //Cancel the event if it's a pig spawner
        if((event.getSpawnReason() == SpawnReason.SPAWNER) && (event.getCreatureType() == CreatureType.PIG)){
            event.setCancelled(true);
        }
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
            ItemStack is = new ItemStack(Material.ROTTEN_FLESH, 1);
            player.getWorld().dropItem(player.getLocation(), is);
            //player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material., 1));
            
        }
    }
}
