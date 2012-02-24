package net.sacredlabyrinth.Phaed.Core;


import com.nilla.vanishnopickup.VanishNoPickup;
import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import in.mDev.MiracleM4n.mChatSuite.mChatSuite;
import net.sacredlabyrinth.Phaed.Core.listeners.CoreEventListener;
import net.sacredlabyrinth.Phaed.Core.managers.CommandManager;
import net.sacredlabyrinth.Phaed.Core.managers.ItemManager;
import net.sacredlabyrinth.Phaed.Core.managers.ItemManager.StackHolder;
import net.sacredlabyrinth.Phaed.Core.managers.PlugManager;
import net.sacredlabyrinth.Phaed.Core.managers.SettingsManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Core for Bukkit
 *
 * @author Phaed
 */
public class Core extends JavaPlugin
{
    private CoreEventListener eventListener;

    public SettingsManager settings;
    public CommandManager cm;
    public PlugManager plm;
    public ItemManager im;
    public static Logger log;
    public VanishNoPickup vanishPlugin;
    public PermissionsPlugin perms;
    public mChatSuite mchatSuite;
    public int[] throughFields = new int[]{0};

    @Override
    public void onEnable()
    {
        eventListener = new CoreEventListener(this);
        settings = new SettingsManager(this);
        cm = new CommandManager(this);
        plm = new PlugManager(this);
        im = new ItemManager();
        log = Logger.getLogger("Minecraft");

        setupVanish();
        setupPermissionsBukkit();
        setupMChatSuite();

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
        Plugin this_plugin = getServer().getPluginManager().getPlugin("VanishNoPickup");

        if (vanishPlugin == null)
        {
            if (this_plugin != null)
            {
                vanishPlugin = ((VanishNoPickup) this_plugin);
            }
            else
            {
                log.info("[" + getDescription().getName() + "] Failed to find VanishNoPickup");
            }
        }
    }

    /*
    * Fake main to allow us to run from netbeans
    */
    public static void main(String[] args)
    {


    }

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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
    {
        try
        {
            String[] split = args;
            String commandName = command.getName().toLowerCase();

            if (commandName.equals("lockdown"))
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

                if (split.length == 0)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /lockdown [message] (keep defaults out)");
                    return true;
                }

                if (!settings.lockDown)
                {
                    settings.lockDown = true;
                    sender.sendMessage("Server is locked down");

                    settings.lockDownMsg = Helper.toMessage(split);

                    List<World> worlds = getServer().getWorlds();

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
            else if (commandName.equals("lockup"))
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

                if (split.length == 0)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /lockup [message]  (keep everyone but defaults out)");
                    return true;
                }

                if (!settings.lockUp)
                {
                    settings.lockUp = true;
                    sender.sendMessage("Server is locked up");

                    settings.lockDownMsg = Helper.toMessage(split);

                    List<World> worlds = getServer().getWorlds();

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
            else if (commandName.equals("data"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (player.hasPermission("core.data"))
                    {
                        TargetBlock tb = new TargetBlock(player, 1000, 0.2, throughFields);

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
            else if (commandName.equals("sun"))
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
            else if (commandName.equals("storm"))
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
            else if (commandName.equals("day"))
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
            else if (commandName.equals("night"))
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
            else if ((commandName.equals("i") || commandName.equals("item")))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (player.hasPermission("core.item"))
                    {
                        if (split.length > 0)
                        {
                            String item = split[0];
                            String count = split.length > 1 && Helper.isInteger(split[1]) ? split[1] : null;

                            ArrayList<StackHolder> stacks = im.getStacks(player, item, count);

                            for (StackHolder stack : stacks)
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
            else if ((commandName.equals("ims") || commandName.equals("items")))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (player.hasPermission("core.items"))
                    {
                        if (split.length > 0)
                        {
                            ArrayList<String> items = new ArrayList<String>();

                            for (int i = 0; i < split.length; i++)
                            {
                                items.add(split[i]);
                            }

                            for (String mitem : items)
                            {
                                ArrayList<StackHolder> stacks = im.getStacks(player, mitem, null);

                                for (StackHolder stack : stacks)
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
            else if (commandName.equals("give"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (player.hasPermission("core.give"))
                    {
                        if (split.length > 1)
                        {
                            Player receiver = Helper.matchUniquePlayer(this, split[0]);

                            if (receiver != null)
                            {
                                String item = split[1];
                                String count = split.length > 2 ? split[2] : null;

                                ArrayList<StackHolder> stacks = im.getStacks(player, item, count);

                                for (StackHolder stack : stacks)
                                {
                                    im.PutStackInHand(receiver, stack.getStack());
                                    Core.log.info("[core] " + player.getName() + " has given " + receiver.getName() + " " + stack.getCount() + " " + Helper.friendlyBlockType(stack.getStack().getType().toString()));
                                    ChatBlock.sendMessage(receiver, ChatColor.LIGHT_PURPLE + player.getName() + " has given you " + stack.getCount() + " " + Helper.friendlyBlockType(stack.getStack().getType().toString()));
                                    ChatBlock.sendMessage(player, ChatColor.LIGHT_PURPLE + "You have given " + receiver.getName() + " " + stack.getCount() + " " + Helper.friendlyBlockType(stack.getStack().getType().toString()));
                                }
                                return true;
                            }

                            player.sendMessage(ChatColor.RED + "Could not find player " + split[0]);
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
            else if (commandName.equals("who") || commandName.equals("list"))
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

                if (split.length > 0)
                {
                    String world = split[0];
                    cm.who(sender, world);
                }
                else
                {
                    cm.who(sender, getServer().getWorlds().get(0).getName());
                }

                return true;
            }
            else if (commandName.equals("maxxp"))
            {
                int iMaxLevel = 50;
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;
                    if (player.hasPermission("core.maxxp"))
                    {
                        Player xpPlayer;
                        if (split.length > 0)
                        {
                            List<Player> matched = getServer().matchPlayer(split[0]);
                            if (matched.isEmpty())
                            {
                                ChatBlock.sendMessage(sender, ChatColor.WHITE + "No Player Matched: " + split[0]);
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

                    if (split.length > 0)
                    {
                        Player xpPlayer;
                        List<Player> matched = getServer().matchPlayer(split[0]);
                        if (matched.isEmpty())
                        {
                            ChatBlock.sendMessage(sender, ChatColor.WHITE + "No Player Matched: " + split[0]);
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
            else if (commandName.equals("msg"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (player.hasPermission("core.msg"))
                    {
                        if (split.length > 1)
                        {
                            String to = split[0];

                            String msg = "";

                            for (int i = 1; i < split.length; i++)
                            {
                                msg += split[i] + " ";
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
            else if (commandName.equals("m"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (player.hasPermission("core.msg"))
                    {
                        if (split.length > 0)
                        {
                            String msg = "";

                            for (int i = 0; i < split.length; i++)
                            {
                                msg += split[i] + " ";
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
            else if (commandName.equals("clear"))
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
            else if (commandName.equals("setrank"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (!player.hasPermission("core.setrank"))
                    {
                        return false;
                    }
                }
                cm.setrank(sender, split);
                return true;
            }
            else if (commandName.equals("plugin"))
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;

                    if (!player.hasPermission("core.plugin"))
                    {
                        return false;
                    }
                }

                if (split.length > 0)
                {
                    String cmd = split[0];

                    if (split.length > 1)
                    {
                        String plug = split[1];

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
            else if (commandName.equals("coords"))
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
            return false;
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
            return true;
        }
    }
}
