package net.sacredlabyrinth.Phaed.Core.managers;

import java.util.ArrayList;

import net.sacredlabyrinth.Phaed.Core.Helper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemManager
{
    public void PutStackInHand(Player player, ItemStack stack)
    {
	ItemStack handitem = player.getItemInHand();
	Inventory inv = player.getInventory();
	
	if (!handitem.getType().equals(Material.AIR))
	{
	    ItemStack inhand = player.getItemInHand();
	    inv.setItem(inv.firstEmpty(), inhand);
	}
	
	player.setItemInHand(stack);
    }
    
    public ArrayList<StackHolder> getStacks(Player player, String mitem, String count)
    {
	ArrayList<StackHolder> out = new ArrayList<StackHolder>();
	ArrayList<String> subitems = new ArrayList<String>();
	
	if (mitem.contains("-"))
	{
	    String[] range = mitem.split("-");
	    
	    if (range.length < 2 || !Helper.isInteger(range[0]) || !Helper.isInteger(range[0]))
	    {
		player.sendMessage(ChatColor.RED + "Invalid range " + mitem);
		return out;
	    }
	    
	    int min = Integer.parseInt(range[0]);
	    int max = Integer.parseInt(range[1]);
	    
	    if (min >= max)
	    {
		player.sendMessage(ChatColor.RED + "Invalid range " + mitem);
		return out;
	    }
	    
	    for (int i = min; i <= max; i++)
	    {
		subitems.add(i + "");
	    }
	}
	else
	{
	    subitems.add(mitem);
	}
	
	for (String item : subitems)
	{
	    Material material = null;
	    int item_count = 1;
	    short data = 0;
	    
	    if (Helper.isInteger(item))
	    {
		material = Material.getMaterial(Integer.parseInt(item));
	    }
	    else if (item.contains(":"))
	    {
		String[] id = mitem.split(":");
		
		material = Material.getMaterial(Integer.parseInt(id[0]));
		data = Short.parseShort(id[1]);
	    }
	    else
	    {		
		material = Material.matchMaterial(item);
	    }
	    
	    if (material == null)
	    {
		player.sendMessage(ChatColor.RED + "Unknown material " + item);
		continue;
	    }
	    
	    if (count != null && Helper.isInteger(count))
	    {
		item_count = Integer.parseInt(count);
	    }
	    else
	    {
		item_count = material.getId() > 255 ? 1 : 64;
	    }
	    
	    int blocks = item_count / 64;
	    
	    for (int i = 0; i < blocks; i++)
	    {
		ItemStack is = new ItemStack(material, 64);
		is.setDurability(data);
		out.add(new StackHolder(is, 64));
	    }
	    
	    int remainder = item_count % 64;
	    
	    if (remainder > 0)
	    {
		ItemStack is = new ItemStack(material, remainder);
		is.setDurability(data);
		out.add(new StackHolder(is, remainder));
	    }
	}
	
	return out;
    }
    
    public class StackHolder
    {
	private ItemStack stack;
	int count;
	
	public StackHolder(ItemStack stack, int count)
	{
	    this.stack = stack;
	    this.count = count;
	}
	
	public ItemStack getStack()
	{
	    return stack;
	}
	
	public int getCount()
	{
	    return count;
	}
    }
}
