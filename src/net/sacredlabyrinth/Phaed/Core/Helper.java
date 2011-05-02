package net.sacredlabyrinth.Phaed.Core;

import java.util.List;

import org.bukkit.entity.Player;

public class Helper
{
    /**
     * Helper function to check for integer
     */
    public static boolean isInteger(Object o)
    {
	return o instanceof java.lang.Integer;
    }
    
    /**
     * Helper function to check for byte
     */
    public static boolean isByte(String input)
    {
	try
	{
	    Byte.parseByte(input);
	    return true;
	}
	catch (Exception ex)
	{
	    return false;
	}
    }
    
    /**
     * Helper function to check for integer
     */
    public static boolean isInteger(String input)
    {
	try
	{
	    Integer.parseInt(input);
	    return true;
	}
	catch (Exception ex)
	{
	    return false;
	}
    }
    
    /**
     * Helper function to check for long
     */
    public static boolean isLong(String input)
    {
	try
	{
	    Long.parseLong(input);
	    return true;
	}
	catch (Exception ex)
	{
	    return false;
	}
    }
    
    /**
     * Helper function to check for string
     */
    public static boolean isString(Object o)
    {
	return o instanceof java.lang.String;
    }
    
    /**
     * Helper function to check for boolean
     */
    public static boolean isBoolean(Object o)
    {
	return o instanceof java.lang.Boolean;
    }
    
    /**
     * Capitalize first word of sentence
     */
    public static String capitalize(String content)
    {
	if (content.length() < 2)
	    return content;
	
	String first = content.substring(0, 1).toUpperCase();
	return first + content.substring(1);
    }
    
    /**
     * Matches the exact player
     */
    public static Player matchExactPlayer(Core plugin, String playername)
    {
	List<Player> players = plugin.getServer().matchPlayer(playername);
	
	for (Player player : players)
	{
	    if (player.getName().equals(playername))
		return player;
	}
	
	return null;
    }
    
    
    /**
     * Match a unique player
     */
    public static Player matchUniquePlayer(Core plugin, String playername)
    {
	List<Player> players = plugin.getServer().matchPlayer(playername);
	
	if(players.size() == 1)
	{
	    return players.get(0);
	}
	
	return null;
    }
    
    /**
     * Convert block type names to friendly format
     */
    public static String friendlyBlockType(String type)
    {
	String out = "";
	
	type = type.toLowerCase().replace('_', ' ');
	
	String[] words = type.split("\\s+");
	
	for(String word : words)
	{
	    out += capitalize(word) + " ";
	}
	
	return out.trim();
    }
    
}
