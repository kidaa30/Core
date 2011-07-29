package net.sacredlabyrinth.Phaed.Core.managers;

import net.sacredlabyrinth.Phaed.Core.Helper;
import net.sacredlabyrinth.Phaed.Core.Core;
import net.sacredlabyrinth.Phaed.Core.ChatBlock;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

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

        player.getWorld().setTime(newtime);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "It is now day");
        return true;
    }

    public boolean night(Player player)
    {
        long curtime = player.getWorld().getTime();
        long newtime = curtime - (curtime % 24000);

        newtime += 14000;

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

            if (plugin.pm.hasPermission(player, "core.admin"))
            {
                isAdmin = true;
            }
        }

        int playerCount = plugin.getServer().getOnlinePlayers().length;
        HashMap<String, HashSet<Player>> groups = new HashMap<String, HashSet<Player>>();
        String playerList = "";

        // sort players into groups

        Player[] online = plugin.getServer().getOnlinePlayers();

        for (int i = 0; i < online.length; i++)
        {
            String group = plugin.pm.permissions.getGroup(world, online[i].getName());

            if (!isAdmin && plugin.vanishPlugin != null && plugin.vanishPlugin.isPlayerInvisible(online[i].getName()) && plugin.vanishPlugin.hidesPlayerFromOnlineList(online[i]))
            {
                playerCount--;
            }
            else if (groups.containsKey(group))
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

        // build the final list with the player's prefix and suffix

        for (String g : groups.keySet())
        {
            for (Player pl : groups.get(g))
            {
                String prefix = plugin.pm.permissions.getGroupPrefix(world, g).replace("&", "\u00a7");
                String suffix = plugin.pm.permissions.getGroupSuffix(world, g).replace("&", "\u00a7");

                if (plugin.vanishPlugin != null && plugin.vanishPlugin.isPlayerInvisible(pl.getName()) && plugin.vanishPlugin.hidesPlayerFromOnlineList(pl))
                {
                    prefix = ChatColor.WHITE + "(vanish)" + prefix;
                }

                playerList += ChatColor.DARK_GRAY + ", " + prefix + pl.getName() + suffix;
            }
        }

        if (playerList.length() == 0)
        {
            playerList = "empty";
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

        if (toplayer == null)
        {
            return false;
        }

        if ((conversations.containsKey(player.getName()) && conversations.get(player.getName()).equals(to)) || (conversations.containsKey(to) && conversations.get(to).equals(player.getName())))
        {
            ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.DARK_GRAY + "(" + ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.LIGHT_PURPLE + toplayer.getName() + ChatColor.DARK_GRAY + ") " + ChatColor.BLUE + msg);
            ChatBlock.sendMessage(toplayer, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.DARK_GRAY + "(" + ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.LIGHT_PURPLE + toplayer.getName() + ChatColor.DARK_GRAY + ") " + ChatColor.BLUE + msg);

            Core.log.info("[msg] (" + player.getName() + ">" + toplayer.getName() + ") " + msg);
            return true;
        }

        if (conversations.containsKey(player.getName()))
        {
            ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.RED + "Ended your conversation with " + conversations.get(player.getName()));

            Player playerother = Helper.matchUniquePlayer(plugin, conversations.get(player.getName()));

            if (playerother != null)
            {
                ChatBlock.sendMessage(playerother, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.RED + Helper.capitalize(player.getName()) + " ended his conversation with you.");
            }
        }

        ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.LIGHT_PURPLE + "Started a conversation. Reply with /m.");
        ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.DARK_GRAY + "(" + ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.LIGHT_PURPLE + toplayer.getName() + ChatColor.DARK_GRAY + ") " + ChatColor.BLUE + msg);

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
        ChatBlock.sendMessage(toplayer, ChatColor.LIGHT_PURPLE + "[msg] " + ChatColor.DARK_GRAY + "(" + ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.LIGHT_PURPLE + toplayer.getName() + ChatColor.DARK_GRAY + ") " + ChatColor.BLUE + msg);

        Core.log.info("[msg] (" + player.getName() + ">" + toplayer.getName() + ") " + msg);

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

        Core.log.info("[msg] (" + player.getName() + ">" + toplayer.getName() + ") " + msg);

        conversations.put(player.getName(), toplayer.getName());
        return true;
    }
}
