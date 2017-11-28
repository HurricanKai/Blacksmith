package net.apunch.blacksmith;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by owner on 11/15/2017.
 */
public class BlacksmithCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(args.length == 3){
            if(args[0].equals("givegems")){
                Player p = Bukkit.getPlayer(args[1]);
                if(p != null){
                    try {
                        int am = Integer.valueOf(args[2]);
                        ItemStack gems = BlacksmithPlugin.itemStackFromSection(BlacksmithPlugin.getItemsConfig().getConfigurationSection("repair-gem"));
                        gems.setAmount(am);
                        p.getInventory().addItem(gems);
                        p.sendMessage(ChatColor.GREEN + "You've received " + am + " Gems of Repair as a reward.");
                    } catch (NumberFormatException e){

                    }
                }
            }
        }
        return true;
    }
}
