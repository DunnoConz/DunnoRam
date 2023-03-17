package dev.DunnoConz.DunnoRam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	@SuppressWarnings("unused")
	private Logger logger = Bukkit.getLogger();
	public Map<Door, Long> brokenDoors;

	@Override
	public void onEnable() {
		brokenDoors = new HashMap<Door, Long>();
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("ram")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("no, you don't have arms.");
				return true;
			}
			Player player = (Player) sender;
			
			player.getInventory().addItem(getItem());
			return true;
		}
		
		return false;
	}
	
	public ItemStack getItem() {
		
		ItemStack ram = new ItemStack(Material.GUNPOWDER);
		ItemMeta meta = ram.getItemMeta();
		
		meta.setDisplayName("Door Ram");
		ArrayList<String> lore = new ArrayList<String>();
		meta.setLore(lore);
		
		ram.setItemMeta(meta);
		
		return ram;
	}
	
	@EventHandler
	public void onPlayerInteraction(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		Block clicked = event.getClickedBlock();
		ItemStack item = player.getInventory().getItemInMainHand();
		
		if (action == Action.LEFT_CLICK_BLOCK && clicked.getType().toString().contains("DOOR")) { // right click + any door types
			Door door = (Door) clicked.getBlockData();
			// lock door in the opposite position
			if (item.getType() == Material.GUNPOWDER && item.getItemMeta().getDisplayName().contains("Door Ram")) {
				if (brokenDoors.get(door) != null && brokenDoors.get(door) > System.currentTimeMillis()) {
					player.sendMessage("The door is already broken!");
					event.setCancelled(true);
					return;
				}
				
				player.sendMessage("Break it down alright!");
				brokenDoors.put(door, System.currentTimeMillis() + 10000);
				return;
			} 
			
			if (brokenDoors.get(door) != null && brokenDoors.get(door) > System.currentTimeMillis())  {
				player.sendMessage("Door is broken! " + String.valueOf(System.currentTimeMillis()) + " " + brokenDoors.get(door));
				event.setCancelled(true);
			}
		}
	}
}
