package net.sacredlabyrinth.Phaed.Core.managers;

import com.platymuus.bukkit.permissions.Group;
import net.sacredlabyrinth.Phaed.Core.ChatBlock;
import net.sacredlabyrinth.Phaed.Core.Core;
import net.sacredlabyrinth.Phaed.Core.Helper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.CraftServer;
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
                String group = gs.get(0).getName();

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

        String[] gs = {"Admins", "Girl/UMods", "VIP/UMods", "UMods", "Girl/SMods", "VIP/SMods", "SMods", "Girl/Mods", "VIP/Mods", "Mods", "Girl/TenuredVeterans", "VIP/TenuredVeterans", "TenuredVeterans", "Girl/Veterans", "VIP/Veterans", "Veterans", "Girl/Members", "VIP/Members", "Members", "default"};

        for (String g : gs)
        {
            HashSet<Player> set = groups.get(g);

            if (set != null)
            {
                for (Player pl : set)
                {
                    String prefix = plugin.mchat.API.getPrefix(pl).replace("&", "\u00a7");
                    String suffix = plugin.mchat.API.getSuffix(pl).replace("&", "\u00a7");

                    if (plugin.vanishPlugin != null && plugin.vanishPlugin.isPlayerInvisible(pl.getName()) && isAdmin)
                    {
                        prefix = ChatColor.WHITE + "(vanish)" + prefix;
                    }
                    else
                    {
                        playerCount++;
                    }

                    playerList += ChatColor.DARK_GRAY + ", " + prefix + pl.getName() + suffix;
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
        ConsoleCommandSender sender = plugin.getServer().getConsoleSender();
        //sender.sendMessage(ChatColor.LIGHT_PURPLE + "[DEBUG msg]" + player.getName() + " (TO): " + ChatColor.WHITE + to + ChatColor.LIGHT_PURPLE + "(MSG): " + ChatColor.WHITE + msg);
        
        Player toplayer = Helper.matchUniquePlayer(plugin, to);

        if ((toplayer == null) || (to == null))
        {
            return false;
        }

        if ((conversations.containsKey(player.getName()) && conversations.get(player.getName()).equals(to)) || (conversations.containsKey(to) && conversations.get(to).equals(player.getName())))
        {
            ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.DARK_GRAY + "(" + ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.LIGHT_PURPLE + toplayer.getName() + ChatColor.DARK_GRAY + ") " + ChatColor.BLUE + msg);
            ChatBlock.sendMessage(toplayer, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.DARK_GRAY + "(" + ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.LIGHT_PURPLE + toplayer.getName() + ChatColor.DARK_GRAY + ") " + ChatColor.BLUE + msg);

            

            sender.sendMessage(ChatColor.LIGHT_PURPLE + "[msg] (" + ChatColor.BLUE + player.getDisplayName() + ChatColor.LIGHT_PURPLE + ">" + toplayer.getDisplayName() + ") " + ChatColor.WHITE + msg);
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

        

        sender.sendMessage(ChatColor.LIGHT_PURPLE + "[msg] (" + ChatColor.BLUE + player.getDisplayName() + ChatColor.LIGHT_PURPLE + ">" + toplayer.getDisplayName() + ") " + ChatColor.WHITE + msg);

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

        CraftServer server = (CraftServer) plugin.getServer();
        ConsoleCommandSender sender = plugin.getServer().getConsoleSender();

        sender.sendMessage(ChatColor.LIGHT_PURPLE + "[msg] (" + ChatColor.BLUE + player.getDisplayName() + ChatColor.LIGHT_PURPLE + ">" + toplayer.getName() + ") " + ChatColor.WHITE + msg);

        conversations.put(player.getName(), toplayer.getName());
        return true;
    }
}
