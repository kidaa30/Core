package net.sacredlabyrinth.Phaed.Core.managers;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionInfo;
import java.util.ArrayList;
import net.sacredlabyrinth.Phaed.Core.ChatBlock;
import net.sacredlabyrinth.Phaed.Core.Core;
import net.sacredlabyrinth.Phaed.Core.Helper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CommandManager extends PlayerListener
{
    private final Core plugin;
    private final HashMap<String, String> conversations = new HashMap<String, String>();

    public CommandManager(Core plugin)
    {
        this.plugin = plugin;
    }

    public boolean sun(Player player)
    {
        
        player.getWorld().setStorm(false);
        player.getWorld().setThundering(false);
        

        player.sendMessage(ChatColor.LIGHT_PURPLE + "It is now Sunny!");
        return true;
    }

    public boolean storm(Player player)
    {
        player.getWorld().setStorm(true);
        player.getWorld().setThundering(true);
        player.getWorld().setThunderDuration(60);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "It is now Stormy!");
        return true;
    }
    
    public boolean day(Player player)
    {
        long curtime = player.getWorld().getTime();
        long newtime = curtime - (curtime % 24000);

        newtime += 0;

        if ((player.getWorld().hasStorm()) || (player.getWorld().isThundering()))
        {
            player.getWorld().setStorm(false);
            player.getWorld().setThundering(false);
        }

        player.getWorld().setTime(newtime);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "It is now day");
        return true;
    }

    public boolean night(Player player)
    {
        long curtime = player.getWorld().getTime();
        long newtime = curtime - (curtime % 24000);

        newtime += 14000;

        if ((player.getWorld().hasStorm()) || (player.getWorld().isThundering()))
        {
            player.getWorld().setStorm(false);
            player.getWorld().setThundering(false);
        }

        player.getWorld().setTime(newtime);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "It is now night");
        return true;
    }
    
    public void who(CommandSender sender, String world)
    {
        boolean isAdmin = false;

        if (sender instanceof Player)
        {
            Player player = (Player) sender;

            if (player.hasPermission("core.admin"))
            {
                isAdmin = true;
            }
        }

        HashMap<String, HashSet<Player>> groups = new HashMap<String, HashSet<Player>>();

        // sort players into groups
        Player[] online = plugin.getServer().getOnlinePlayers();

        for (int i = 0; i < online.length; i++)
        {
            List<Group> gs = plugin.perms.getGroups(online[i].getName());

            if (!gs.isEmpty())
            {
                Group g = gs.get(0);
                String group = g.getName();
                
                if (groups.containsKey(group))
                {
                    ((HashSet) groups.get(group)).add(online[i]);
                }
                else
                {
                    HashSet players = new HashSet();
                    players.add(online[i]);
                    groups.put(group, players);
                }
            }
        }

        // sort players into groups

        String playerList = "";
        int playerCount = 0;
        
        List<Group> ordered_groups = plugin.perms.getAllGroups();
        
       for(int i = ordered_groups.size() - 1; i >=0; i-- ){
           Group grr = ordered_groups.get(i); 
           String g = grr.getName();
           
            HashSet<Player> set = groups.get(g);

            if (set != null)
            {
                for (Player pl : set)
                {   
                    String mName = "";
                    if(plugin.mchatSuite != null){
                        mName = plugin.mchatSuite.getAPI().ParsePlayerName(pl.getName(), pl.getWorld().getName());
                    }
                    else if(plugin.mchat != null){
                        mName = plugin.mchat.API.getPrefix(pl).replace("&", "\u00a7") + pl.getName() +  plugin.mchat.API.getSuffix(pl).replace("&", "\u00a7");
                    }

                    
                    

                    if (plugin.vanishPlugin != null && plugin.vanishPlugin.isPlayerInvisible(pl.getName()) && isAdmin)
                    {
                        mName = ChatColor.WHITE + "(vanish)" + mName;
                    }
                    else
                    {
                        playerCount++;
                    }

                    playerList += ChatColor.DARK_GRAY + ", " + mName;
                }
            }
        }

        if (playerList.length() == 0)
        {
            playerList = "noone";
        }
        else
        {
            playerList = playerList.substring(4);
        }

        ChatBlock.sendMessage(sender, ChatColor.WHITE + "Who's online " + ChatColor.GRAY + "(" + playerCount + "/" + plugin.getServer().getMaxPlayers() + "): " + playerList);
    }

    public boolean msg(Player player, String to, String msg)
    {
        Player toplayer = Helper.matchUniquePlayer(plugin, to);

        if ((toplayer == null) || (to == null))
        {
            return false;
        }

        if ((conversations.containsKey(player.getName()) && conversations.get(player.getName()).equals(to)) || (conversations.containsKey(to) && conversations.get(to).equals(player.getName())))
        {
            ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.DARK_GRAY + "(" + ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.LIGHT_PURPLE + toplayer.getName() + ChatColor.DARK_GRAY + ") " + ChatColor.BLUE + msg);
            ChatBlock.sendMessage(toplayer, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.DARK_GRAY + "(" + ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.LIGHT_PURPLE + toplayer.getName() + ChatColor.DARK_GRAY + ") " + ChatColor.BLUE + msg);

            
            
            plugin.log.info(ChatColor.LIGHT_PURPLE + "[msg] (" + ChatColor.BLUE + player.getDisplayName() + ChatColor.LIGHT_PURPLE + ">" + toplayer.getDisplayName() + ") " + ChatColor.WHITE + msg);
            return true;
        }

        if (conversations.containsKey(player.getName()))
        {
            ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.RED + "Ended your conversation with " + conversations.get(player.getName()));

            Player playerother = Helper.matchUniquePlayer(plugin, conversations.get(player.getName()));

            if (playerother != null)
            {
                ChatBlock.sendMessage(playerother, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.RED + Helper.capitalize(player.getDisplayName()) + " ended his conversation with you.");
            }
        }

        ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.LIGHT_PURPLE + "Started a conversation. Reply with /m.");
        ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.DARK_GRAY + "(" + ChatColor.BLUE + player.getDisplayName() + ChatColor.DARK_GRAY + ">" + ChatColor.LIGHT_PURPLE + toplayer.getDisplayName() + ChatColor.DARK_GRAY + ") " + ChatColor.BLUE + msg);

        if (conversations.containsKey(toplayer.getName()))
        {
            ChatBlock.sendMessage(toplayer, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.RED + "Ended your conversation with " + conversations.get(toplayer.getName()));

            Player playerother = Helper.matchUniquePlayer(plugin, conversations.get(toplayer.getName()));

            if (playerother != null)
            {
                ChatBlock.sendMessage(playerother, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.RED + Helper.capitalize(toplayer.getName()) + " ended his conversation with you.");
            }
        }

        ChatBlock.sendMessage(toplayer, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.LIGHT_PURPLE + "A conversation was started with you. Reply with /m.");
        ChatBlock.sendMessage(toplayer, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.DARK_GRAY + "(" + ChatColor.BLUE + player.getDisplayName() + ChatColor.DARK_GRAY + ">" + ChatColor.LIGHT_PURPLE + toplayer.getDisplayName() + ChatColor.DARK_GRAY + ") " + ChatColor.BLUE + msg);

        plugin.log.info(ChatColor.LIGHT_PURPLE + "[msg] (" + ChatColor.BLUE + player.getDisplayName() + ChatColor.LIGHT_PURPLE + ">" + toplayer.getDisplayName() + ") " + ChatColor.WHITE + msg);

        conversations.put(player.getName(), toplayer.getName());
        conversations.put(toplayer.getName(), player.getName());

        return true;
    }

    public boolean m(Player player, String msg)
    {
        if (!conversations.containsKey(player.getName()))
        {
            return false;
        }

        Player toplayer = Helper.matchUniquePlayer(plugin, conversations.get(player.getName()));

        if (toplayer == null)
        {
            return false;
        }

        ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.DARK_GRAY + "(" + ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.LIGHT_PURPLE + toplayer.getName() + ChatColor.DARK_GRAY + ") " + ChatColor.BLUE + msg);
        ChatBlock.sendMessage(toplayer, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.DARK_GRAY + "(" + ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.LIGHT_PURPLE + toplayer.getName() + ChatColor.DARK_GRAY + ") " + ChatColor.BLUE + msg);


        plugin.log.info(ChatColor.LIGHT_PURPLE + "[msg] (" + ChatColor.BLUE + player.getDisplayName() + ChatColor.LIGHT_PURPLE + ">" + toplayer.getName() + ") " + ChatColor.WHITE + msg);

        conversations.put(player.getName(), toplayer.getName());
        return true;
    }

    public void setrank(CommandSender sender, String[] split) {
        
        plugin.log.info(ChatColor.LIGHT_PURPLE + "[setrank]: " + Helper.toMessage(split));
        
        
        if (split.length > 0)
        {
            String playername = split[0];
            if (split.length > 1)
            {
                String groupname = split[1];
                List<Group> PlayerGroups = plugin.perms.getGroups(playername);
                if((PlayerGroups == null) || (PlayerGroups.isEmpty()))
                {
                    ChatBlock.sendMessage(sender, "[setrank] " + ChatColor.LIGHT_PURPLE + " Could not find player: '" + playername + "'");
                    return;
                }
                List<Group> ServerGroups = plugin.perms.getAllGroups();

                
                
                boolean bGroupExists = false;
                for(Group oGroup : ServerGroups){
                    //Need to make sure we have the groupname on the server
                    if(oGroup.getName().equals(groupname)){
                        bGroupExists = true;
                        break;
                    }
                }
                if(!bGroupExists){
                    ChatBlock.sendMessage(sender, "[setrank] " + ChatColor.LIGHT_PURPLE + " Could not find group: '" + groupname + "'");
                    return;
                }
                
                List<String> lsPermsCommands = new ArrayList<String>();
                
                //if we already had the group to ignore, add it back into the permissions
                
                lsPermsCommands.add("perm player setgroup " + playername + " " + groupname);
                //Re-add all the other groups the player had
                int iCount = 0;
                if(PlayerGroups.size() > 1){
                    while(iCount < PlayerGroups.size()){
                        if(iCount > 0){
                            lsPermsCommands.add("perm player addgroup " + playername + " " + PlayerGroups.get(iCount).getName());
                        }
                        iCount += 1;
                    }
                }           
                
                for(String sCmd : lsPermsCommands){
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), sCmd);
                    ChatBlock.sendMessage(sender, "[setrank] " + ChatColor.LIGHT_PURPLE + " Command Sent to setrank: '" + sCmd + "'");
                }
            }
        }
    }
}
