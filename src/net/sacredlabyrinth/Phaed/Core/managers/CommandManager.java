package net.sacredlabyrinth.Phaed.Core.managers;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import in.mDev.MiracleM4n.mChatSuite.api.mChatAPI;
import net.sacredlabyrinth.Phaed.Core.ChatBlock;
import net.sacredlabyrinth.Phaed.Core.Core;
import net.sacredlabyrinth.Phaed.Core.Helper;
import net.sacredlabyrinth.Phaed.Core.TargetBlock;
import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

public class CommandManager implements CommandExecutor
{
    private final Core plugin;
    private final HashMap<String, String> conversations = new HashMap<String, String>();
    private SettingsManager settings;
    private CommandManager cm;
    private PlugManager plm;
    private ItemManager im;
    private PermissionsPlugin perms;

    public CommandManager(Core plugin)
    {
        this.plugin = plugin;
        settings = plugin.getSettings();
        perms = plugin.getPerms();
        cm = plugin.getCm();
        im = plugin.getIm();
        plm = plugin.getPlm();
    }

    /**
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        try
        {
            if (command.getName().equals("lockdown"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (!player.hasPermission("core.lockdown"))
                    {
                        return false;
                    }
                }

                if (settings.lockDown)
                {
                    settings.lockDown = false;
                    sender.sendMessage("Server is open");
                    return true;
                }

                if (args.length == 0)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /lockdown [message] (keep defaults out)");
                    return true;
                }

                if (!settings.lockDown)
                {
                    settings.lockDown = true;
                    sender.sendMessage("Server is locked down");

                    settings.lockDownMsg = Helper.toMessage(args);

                    List<World> worlds = plugin.getServer().getWorlds();

                    for (World world : worlds)
                    {
                        List<Player> players = world.getPlayers();

                        for (Player p : players)
                        {
                            List<Group> gs = perms.getGroups(p.getName());

                            if (gs.size() == 1)
                            {
                                if (gs.get(0).getName().equalsIgnoreCase("default"))
                                {
                                    p.kickPlayer(settings.lockDownMsg);
                                }
                            }
                        }
                    }
                }
            }
            else if (command.getName().equals("lockup"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (!player.hasPermission("core.lockup"))
                    {
                        return false;
                    }
                }

                if (settings.lockUp)
                {
                    sender.sendMessage("Server is open");
                    return true;
                }

                if (args.length == 0)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /lockup [message]  (keep everyone but defaults out)");
                    return true;
                }

                if (!settings.lockUp)
                {
                    settings.lockUp = true;
                    sender.sendMessage("Server is locked up");

                    settings.lockDownMsg = Helper.toMessage(args);

                    List<World> worlds = plugin.getServer().getWorlds();

                    for (World world : worlds)
                    {
                        List<Player> players = world.getPlayers();

                        for (Player p : players)
                        {
                            List<Group> gs = perms.getGroups(p.getName());

                            if (gs.size() == 1)
                            {
                                if (!gs.get(0).getName().equalsIgnoreCase("default"))
                                {
                                    p.kickPlayer(settings.lockDownMsg);
                                }
                            }
                        }
                    }
                }
            }
            else if (command.getName().equals("data"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (player.hasPermission("core.data"))
                    {
                        TargetBlock tb = new TargetBlock(player, 1000, 0.2, plugin.getThroughFields());

                        if (tb != null)
                        {
                            Block target = tb.getTargetBlock();
                            player.sendMessage(ChatColor.YELLOW + "Type: " + ChatColor.AQUA + target.getType() + ChatColor.YELLOW + " Data: " + ChatColor.AQUA + target.getData());
                            Core.log.info("Type: " + target.getType() + " Data: " + target.getData());
                        }
                        else
                        {
                            player.sendMessage(ChatColor.RED + "Not pointing at a block");
                        }

                        return true;
                    }
                }
                else
                {
                    sender.sendMessage("Command requires a player");
                }
            }
            else if (command.getName().equals("sun"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (player.hasPermission("core.sun"))
                    {
                        cm.sun(player);
                        Core.log.info("[core] " + player.getName() + " set world " + player.getWorld().getName() + " to sunny");
                        return true;
                    }
                }
                else
                {
                    sender.sendMessage("Command requires a player");
                }
            }
            else if (command.getName().equals("storm"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (player.hasPermission("core.storm"))
                    {
                        cm.storm(player);
                        Core.log.info("[core] " + player.getName() + " set world " + player.getWorld().getName() + " to stormy");
                        return true;
                    }
                }
                else
                {
                    sender.sendMessage("Command requires a player");
                }
            }
            else if (command.getName().equals("day"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (player.hasPermission("core.day"))
                    {
                        cm.day(player);
                        Core.log.info("[core] " + player.getName() + " changed time to day");
                        return true;
                    }
                }
                else
                {
                    sender.sendMessage("Command requires a player");
                }
            }
            else if (command.getName().equals("night"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (player.hasPermission("core.night"))
                    {
                        cm.night(player);
                        Core.log.info("[core] " + player.getName() + " changed time to night");
                        return true;
                    }
                }
                else
                {
                    sender.sendMessage("Command requires a player");
                }
            }
            else if ((command.getName().equals("i") || command.getName().equals("item")))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (player.hasPermission("core.item"))
                    {
                        if (args.length > 0)
                        {
                            String item = args[0];
                            String count = args.length > 1 && Helper.isInteger(args[1]) ? args[1] : null;

                            ArrayList<ItemManager.StackHolder> stacks = im.getStacks(player, item, count);

                            for (ItemManager.StackHolder stack : stacks)
                            {
                                im.PutStackInHand(player, stack.getStack());
                                Core.log.info("[core] " + player.getName() + " spawned " + stack.getCount() + " " + Helper.friendlyBlockType(stack.getStack().getType().toString()));
                                ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "You got " + stack.getCount() + " " + Helper.friendlyBlockType(stack.getStack().getType().toString()));
                            }
                            return true;
                        }
                    }
                    player.sendMessage(ChatColor.RED + "Usage: /item [id|id-range|name] <count>");
                }
                else
                {
                    sender.sendMessage("Command requires a player");
                }
            }
            else if ((command.getName().equals("ims") || command.getName().equals("items")))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (player.hasPermission("core.items"))
                    {
                        if (args.length > 0)
                        {
                            ArrayList<String> items = new ArrayList<String>();

                            for (int i = 0; i < args.length; i++)
                            {
                                items.add(args[i]);
                            }

                            for (String mitem : items)
                            {
                                ArrayList<ItemManager.StackHolder> stacks = im.getStacks(player, mitem, null);

                                for (ItemManager.StackHolder stack : stacks)
                                {
                                    im.PutStackInHand(player, stack.getStack());
                                    Core.log.info("[core] " + player.getName() + " spawned " + stack.getCount() + " " + Helper.friendlyBlockType(stack.getStack().getType().toString()));
                                    ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "You got " + stack.getCount() + " " + Helper.friendlyBlockType(stack.getStack().getType().toString()));
                                }
                            }
                            return true;
                        }
                    }
                    player.sendMessage(ChatColor.RED + "Usage: /items [id|id-range|name] [id|id-range|name] ...");
                }
                else
                {
                    sender.sendMessage("Command requires a player");
                }
            }
            else if (command.getName().equals("give"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (player.hasPermission("core.give"))
                    {
                        if (args.length > 1)
                        {
                            Player receiver = Helper.matchUniquePlayer(plugin, args[0]);

                            if (receiver != null)
                            {
                                String item = args[1];
                                String count = args.length > 2 ? args[2] : null;

                                ArrayList<ItemManager.StackHolder> stacks = im.getStacks(player, item, count);

                                for (ItemManager.StackHolder stack : stacks)
                                {
                                    im.PutStackInHand(receiver, stack.getStack());
                                    Core.log.info("[core] " + player.getName() + " has given " + receiver.getName() + " " + stack.getCount() + " " + Helper.friendlyBlockType(stack.getStack().getType().toString()));
                                    ChatBlock.sendMessage(receiver, ChatColor.LIGHT_PURPLE + player.getName() + " has given you " + stack.getCount() + " " + Helper.friendlyBlockType(stack.getStack().getType().toString()));
                                    ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "You have given " + receiver.getName() + " " + stack.getCount() + " " + Helper.friendlyBlockType(stack.getStack().getType().toString()));
                                }
                                return true;
                            }

                            player.sendMessage(ChatColor.RED + "Could not find player " + args[0]);
                            return true;
                        }
                    }
                    player.sendMessage(ChatColor.RED + "Usage: /give [player] [id|id-range|name] [count] ...");
                }
                else
                {
                    sender.sendMessage("Command requires a player");
                }
            }
            else if (command.getName().equals("who") || command.getName().equals("list"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (player.hasPermission("core.who"))
                    {
                        cm.who(player, player.getWorld().getName());
                    }

                    return true;
                }

                if (args.length > 0)
                {
                    String world = args[0];
                    cm.who(sender, world);
                }
                else
                {
                    cm.who(sender, plugin.getServer().getWorlds().get(0).getName());
                }

                return true;
            }
            else if (command.getName().equals("maxxp"))
            {
                int iMaxLevel = 50;
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;
                    if (player.hasPermission("core.maxxp"))
                    {
                        Player xpPlayer;
                        if (args.length > 0)
                        {
                            List<Player> matched = plugin.getServer().matchPlayer(args[0]);
                            if (matched.isEmpty())
                            {
                                ChatBlock.sendMessage(sender, ChatColor.WHITE + "No Player Matched: " + args[0]);
                                return true;
                            }
                            xpPlayer = matched.get(0);
                        }
                        else
                        {
                            xpPlayer = player;
                        }
                        //ChatBlock.sendMessage(sender, ChatColor.WHITE + "Current Total Experience and Level " + xpPlayer.getTotalExperience() + " : " + xpPlayer.getLevel());
                        xpPlayer.setLevel(iMaxLevel);

                        //xpPlayer.setTotalExperience(100);
                        //xpPlayer.setLevel(100);
                        //xpPlayer.setExperience(10);

                        ChatBlock.sendMessage(sender, ChatColor.WHITE + xpPlayer.getName() + " XP Level set to " + ChatColor.GOLD + iMaxLevel);
                    }


                }
                else
                {

                    if (args.length > 0)
                    {
                        Player xpPlayer;
                        List<Player> matched = plugin.getServer().matchPlayer(args[0]);
                        if (matched.isEmpty())
                        {
                            ChatBlock.sendMessage(sender, ChatColor.WHITE + "No Player Matched: " + args[0]);
                            return true;
                        }
                        xpPlayer = matched.get(0);
                        xpPlayer.setLevel(iMaxLevel);
                        ChatBlock.sendMessage(sender, ChatColor.WHITE + xpPlayer.getName() + " XP Level set to " + ChatColor.GOLD + iMaxLevel);
                    }
                    else
                    {
                        sender.sendMessage("Command requires a player");
                    }


                }

                return true;
            }
            else if (command.getName().equals("msg"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (player.hasPermission("core.msg"))
                    {
                        if (args.length > 1)
                        {
                            String to = args[0];

                            String msg = "";

                            for (int i = 1; i < args.length; i++)
                            {
                                msg += args[i] + " ";
                            }

                            msg = msg.trim();

                            if (!cm.msg(player, to, msg))
                            {
                                player.sendMessage(ChatColor.RED + "There are no players matching that name");
                            }
                            return true;
                        }
                    }
                    player.sendMessage(ChatColor.RED + "Usage: /msg [player] [message]");
                }
                else
                {
                    sender.sendMessage("Command requires a player");
                }
            }
            else if (command.getName().equals("m"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (player.hasPermission("core.msg"))
                    {
                        if (args.length > 0)
                        {
                            String msg = "";

                            for (int i = 0; i < args.length; i++)
                            {
                                msg += args[i] + " ";
                            }

                            msg = msg.trim();

                            if (!cm.m(player, msg))
                            {
                                player.sendMessage(ChatColor.RED + "You are not in a conversation");
                            }
                            return true;
                        }
                    }
                    player.sendMessage(ChatColor.RED + "Usage: /m [message]");
                }
                else
                {
                    sender.sendMessage("Command requires a player");
                }
            }
            else if (command.getName().equals("clear"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (player.hasPermission("core.clear"))
                    {
                        player.getInventory().clear();
                        ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "Inventory cleared");
                        return true;
                    }
                }
                else
                {
                    sender.sendMessage("Command requires a player");
                }
            }
            else if (command.getName().equals("setrank"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (!player.hasPermission("core.setrank"))
                    {
                        return false;
                    }
                }
                cm.setrank(sender, args);
                return true;
            }
            else if (command.getName().equals("plugin"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (!player.hasPermission("core.plugin"))
                    {
                        return false;
                    }
                }

                if (args.length > 0)
                {
                    String cmd = args[0];

                    if (args.length > 1)
                    {
                        String plug = args[1];

                        if (cmd.equals("load"))
                        {
                            plm.loadPlugin(plug, sender);
                            return true;
                        }
                        if (cmd.equals("reload"))
                        {
                            plm.reloadPlugin(plug, sender);
                            return true;
                        }
                        if (cmd.equals("enable"))
                        {
                            plm.enablePlugin(plug, sender);
                            return true;
                        }
                        if (cmd.equals("disable"))
                        {
                            plm.disablePlugin(plug, sender);
                            return true;
                        }
                    }

                    if (cmd.equals("list"))
                    {
                        plm.listPlugins(sender);
                        return true;
                    }
                }

                sender.sendMessage(ChatColor.RED + "Usage: /plugin [load|reload|enable|disable|list] [plugin]");
            }
            else if (command.getName().equals("coords"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (!player.hasPermission("core.coords"))
                    {
                        return false;
                    }
                }

                if (sender instanceof Player)
                {
                    Player plr = (Player) sender;
                    Location plrloc = plr.getLocation();
                    plr.sendMessage(ChatColor.AQUA + "You are at X: " + plrloc.getBlockX() + " Y: " + plrloc.getBlockY() + " Z: " + plrloc.getBlockZ());
                }
                else
                {
                    sender.sendMessage("Command requires a player");
                }
                return true;
            }

        }
        catch (Exception ex)
        {
            PreciousStones.log(Level.SEVERE, "Command failure: {0}", ex.getMessage());
        }

        return false;
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
            List<Group> gs = plugin.getPerms().getGroups(online[i].getName());

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

        List<Group> ordered_groups = plugin.getPerms().getAllGroups();

        for (int i = ordered_groups.size() - 1; i >= 0; i--)
        {
            Group grr = ordered_groups.get(i);
            String g = grr.getName();

            HashSet<Player> set = groups.get(g);

            if (set != null)
            {
                boolean bmChatFailed = false;
                for (Player pl : set)
                {

                    String mName = "";

                    if (!bmChatFailed)
                    {
                        try
                        {
                            if (plugin.mchatSuite != null)
                            {
                                mChatAPI api = plugin.mchatSuite.getAPI();

                                mName = api.ParsePlayerName(pl.getName(), pl.getWorld().getName());
                            }
                        }
                        catch (Exception ex)
                        {
                            bmChatFailed = true;
                        }
                    }
                    else
                    {
                        mName = pl.getName();
                    }

                    try{
                        if (plugin.vanishPlugin != null && plugin.vanishPlugin.isVanished(pl.getName()) && isAdmin)
                        {
                            mName = ChatColor.WHITE + "(vanish)" + mName;
                        }
                        else
                        {
                            playerCount++;
                        }
                    }
                    catch(Exception ex){

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

    public void setrank(CommandSender sender, String[] args)
    {
        plugin.log.info(ChatColor.LIGHT_PURPLE + "[setrank]: " + Helper.toMessage(args));

        if (args.length > 0)
        {
            String playername = args[0];
            if (args.length > 1)
            {
                String groupname = args[1];
                List<Group> PlayerGroups = plugin.getPerms().getGroups(playername);
                if ((PlayerGroups == null) || (PlayerGroups.isEmpty()))
                {
                    ChatBlock.sendMessage(sender, "[setrank] " + ChatColor.LIGHT_PURPLE + " Could not find player: '" + playername + "'");
                    return;
                }
                List<Group> ServerGroups = plugin.getPerms().getAllGroups();


                boolean bGroupExists = false;
                for (Group oGroup : ServerGroups)
                {
                    //Need to make sure we have the groupname on the server
                    if (oGroup.getName().equals(groupname))
                    {
                        bGroupExists = true;
                        break;
                    }
                }
                if (!bGroupExists)
                {
                    ChatBlock.sendMessage(sender, "[setrank] " + ChatColor.LIGHT_PURPLE + " Could not find group: '" + groupname + "'");
                    return;
                }

                List<String> lsPermsCommands = new ArrayList<String>();

                //if we already had the group to ignore, add it back into the permissions

                lsPermsCommands.add("perm player setgroup " + playername + " " + groupname);
                //Re-add all the other groups the player had
                int iCount = 0;
                if (PlayerGroups.size() > 1)
                {
                    while (iCount < PlayerGroups.size())
                    {
                        if (iCount > 0)
                        {
                            lsPermsCommands.add("perm player addgroup " + playername + " " + PlayerGroups.get(iCount).getName());
                        }
                        iCount += 1;
                    }
                }

                for (String sCmd : lsPermsCommands)
                {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), sCmd);
                    ChatBlock.sendMessage(sender, "[setrank] " + ChatColor.LIGHT_PURPLE + " Command Sent to setrank: '" + sCmd + "'");
                }
            }
        }
    }
}
