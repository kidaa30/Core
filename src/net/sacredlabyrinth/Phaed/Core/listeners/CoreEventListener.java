package net.sacredlabyrinth.Phaed.Core.listeners;


import com.platymuus.bukkit.permissions.Group;
import java.util.List;

import net.sacredlabyrinth.Phaed.Core.Core;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.inventory.ItemStack;

public class CoreEventListener implements Listener
{
    private final Core plugin;

    /**
     * @param plugin
     */
    public CoreEventListener(Core plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        //Cancel the event if it's NOT a blaze
         if((event.getSpawnReason() == SpawnReason.SPAWNER) && (event.getEntityType() == EntityType.PIG)){
             event.setCancelled(true);
         }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (event.getEntity() instanceof Player)
        {

            Player player = (Player) event.getEntity();


            int n = player.getLevel();
            //total exp =7n + round(3.5(.5(n^2)-.5n)) - n/4)
            double totalLevelEXP = 7*n + Math.round((3.5F * (0.5F * (n*n) - 0.5F * n)) - n/4);

            //Spawn 3 mobs per player level minimum of one
            int iNumMobs = 1;
            if(n > 3){
                iNumMobs = n / 3;
            }
            for(int i=0;i<iNumMobs;i++){
                player.getWorld().spawnCreature(player.getLocation(), EntityType.ZOMBIE);
            }


            //Then let's take that XP and do 80% of it
            totalLevelEXP = 0.8F * totalLevelEXP;
            event.setDroppedExp((int)totalLevelEXP);
            //player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material., 1));

        }
        else {
            EntityDamageEvent ed = event.getEntity().getLastDamageCause();
            if(ed == null){
                return;
            }
            DamageCause dc = ed.getCause();
            if((dc == DamageCause.MAGIC) || (dc == DamageCause.POISON)){
                event.setDroppedExp(0);
            }

            //Instead of just dying, Creepers explode with the power of the explosion damage that killed them
           /* if((event.getEntity() instanceof Creeper) && ((dc == DamageCause.ENTITY_EXPLOSION) || (dc == DamageCause.BLOCK_EXPLOSION))){
                Location l = event.getEntity().getLocation();

                World wld = plugin.getServer().getWorld(l.getWorld().getName());
                Explosion xpl = new Explosion(wld,(Entity)event.getEntity(), l.getX(), l.getY(), l.getZ(), 3.0F);
                xpl.a(true);

            }*/
        }
    }

    @EventHandler
    public void onPlayerPreLogin(PlayerPreLoginEvent event)
    {
        if (plugin.getSettings().lockDown)
        {
            event.setKickMessage(plugin.getSettings().lockDownMsg);

            List<Group> gs = plugin.getPerms().getGroups(event.getName());

            if (gs.size() == 1)
            {
                if (gs.get(0).getName().equalsIgnoreCase("default"))
                {
                    event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, plugin.getSettings().lockDownMsg);
                    Core.log.info("Player " + event.getName() + " was locked out");
                }
            }
        }

        if (plugin.getSettings().lockUp)
        {
            event.setKickMessage(plugin.getSettings().lockDownMsg);

            List<Group> gs = plugin.getPerms().getGroups(event.getName());

            if (gs.size() == 1)
            {
                if (gs.get(0).getName().equalsIgnoreCase("default"))
                {
                    event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, plugin.getSettings().lockDownMsg);
                    Core.log.info("Player " + event.getName() + " was locked out");
                }
            }
        }
    }
}
