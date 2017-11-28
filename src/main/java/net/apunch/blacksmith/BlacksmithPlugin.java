package net.apunch.blacksmith;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.apunch.blacksmith.util.Settings;
import net.apunch.blacksmith.util.Settings.Setting;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.DataKey;

import net.milkbowl.vault.economy.Economy;

import net.milkbowl.vault.item.Items;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public class BlacksmithPlugin extends JavaPlugin implements CommandExecutor {
	//TODO: player.getInventory().getItem(index) Index = onItemHeldChange(EVENT);
	public BlacksmithPlugin plugin;
	private Settings config;
	private Economy economy;
	private boolean useHyperAPI = false;
	private static File itemsFile;
	private static FileConfiguration itemsConfig;
    //private boolean hasCititrader = false; // CitiTrader dependency outdated and broken

	@Override
	public void onDisable() {
	//	config.save();

		getLogger().log(Level.INFO, " v" + getDescription().getVersion() + " disabled.");
	}

	@Override
	public void onEnable() {
		getCommand("blacksmith").setExecutor(new BlacksmithCommand());
		config = new Settings(this);
		config.load();
		itemsFile = new File(this.getDataFolder(), "items.yml");
		if(!itemsFile.exists())
			try {
				FileUtils.copyInputStreamToFile(this.getResource("items.yml"), itemsFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);
		// Setup Hyperconomy (Soft-Depend only, so this is completely optional!)    
		// Hyperconomy uses your favorite Vault-compatible economy system
		// and calculates prices for items based on supply and demand on the fly.
		// This is only used to get the cost of a repair.

		getLogger().log(Level.INFO, "Setting Up Vault now....");
        /* CitiTrader dependency outdated and broken
                // Check for Cititrader
                 if(getServer().getPluginManager().getPlugin("CitiTrader") != null) {
                     hasCititrader = true;
                 }
                 */
                
		// Setup Vault
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(
		Economy.class);
		if (economyProvider != null)
			economy = economyProvider.getProvider();
		else {
			// Disable if no economy plugin was found
			getServer().getLogger().log(Level.SEVERE, "Failed to load an economy plugin. Disabling...");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
				
		CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(BlacksmithTrait.class).withName("blacksmith"));

		getLogger().log(Level.INFO, " v" + getDescription().getVersion() + " enabled.");
	}

	public static FileConfiguration getItemsConfig(){
		return itemsConfig;
	}

	public static void saveItemConfig(FileConfiguration conf){
		try {
			itemsConfig.save(itemsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		
        if(command.getName().equalsIgnoreCase("bs"))
        {
        if (args.length < 1)
        {
        	sender.sendMessage("Please Specify a Mode");
        	return false;
        }
        if (args.length < 2)
        {
        	sender.sendMessage("Please Specify a Node");
        	return false;
        }
		if (args[0].equalsIgnoreCase("config"))
		{
			if (args[1].equalsIgnoreCase("reload"))
			{
				if (!sender.hasPermission("bs.reload")) return true;
				//reset
				config = new Settings(this);
				//load
				config.load();
		        sender.sendMessage(ChatColor.GREEN + "Blacksmith config reloaded!");
		        return true;
			}
			if (args[1].equalsIgnoreCase("reset"))
			{
				if (!sender.hasPermission("bs.reset")) return true;
				config.Reset();
				sender.sendMessage(ChatColor.RED + "Blacksmith config Resetet");
				return true;
			}
		}
		else
		{
			sender.sendMessage("Please Specify a Valid Mode");
			sender.sendMessage(args[0] + args[1]);
			return false;
		}
        }
        return false;
    }

    /* CitiTrader dependency outdated and broken
    // Return if we have cititrader
         public boolean hasCititrader() {
            return this.hasCititrader;
         }
         */
        
	public BlacksmithTrait getBlacksmith(NPC npc){

		if (npc !=null && npc.hasTrait(BlacksmithTrait.class)){
			return npc.getTrait(BlacksmithTrait.class);
		}

		return null;
	}

	public String FormatBalance(Player player)
	{
		double balance = getbalance(player);
		return Double.toString(balance);
	}
	
	private double getbalance(Player player)
	{
		return economy.getBalance((OfflinePlayer) player);
	}
	public boolean isTool(ItemStack item) {
		switch (item.getType()) {
		case WOOD_PICKAXE:
		case WOOD_SPADE:
		case WOOD_HOE:
		case WOOD_SWORD:
		case WOOD_AXE:
		case STONE_PICKAXE:
		case STONE_SPADE:
		case STONE_HOE:
		case STONE_SWORD:
		case STONE_AXE:
		case GOLD_PICKAXE:
		case GOLD_SPADE:
		case GOLD_HOE:
		case GOLD_SWORD:
		case GOLD_AXE:
		case IRON_PICKAXE:
		case IRON_SPADE:
		case IRON_HOE:
		case IRON_SWORD:
		case IRON_AXE:
		case DIAMOND_PICKAXE:
		case DIAMOND_SPADE:
		case DIAMOND_HOE:
		case DIAMOND_SWORD:
		case DIAMOND_AXE:
		case BOW:
		case FLINT_AND_STEEL:
		case FISHING_ROD:
		case SHEARS:
		case ELYTRA:
			return true;
		default:
			return false;
		}
	}

	public boolean isArmor(ItemStack item) {
		switch (item.getType()) {
		case LEATHER_HELMET:
		case LEATHER_CHESTPLATE:
		case LEATHER_LEGGINGS:
		case LEATHER_BOOTS:
		case CHAINMAIL_HELMET:
		case CHAINMAIL_CHESTPLATE:
		case CHAINMAIL_LEGGINGS:
		case CHAINMAIL_BOOTS:
		case GOLD_HELMET:
		case GOLD_CHESTPLATE:
		case GOLD_LEGGINGS:
		case GOLD_BOOTS:
		case IRON_HELMET:
		case IRON_CHESTPLATE:
		case IRON_LEGGINGS:
		case IRON_BOOTS:
		case DIAMOND_HELMET:
		case DIAMOND_CHESTPLATE:
		case DIAMOND_LEGGINGS:
		case DIAMOND_BOOTS:
		case ELYTRA:
			return true;
		default:
			return false;
		}
	}

	public boolean doesPlayerHaveEnough(Player player) {
		return economy.getBalance((OfflinePlayer) player) - getCost(player.getItemInHand(), player) >= 0;
	}

	public String formatCost(Player player) {
		double cost = getCost(player.getItemInHand(), player);
		return economy.format(cost);
	}

	public void withdraw(Player player) {
		economy.withdrawPlayer(((OfflinePlayer) player), getCost(player.getItemInHand(), player));
	}
       /* CitiTrader dependency outdated and broken.
        public void deposit(NPC npc, Player player) {
//            if(hasCititrader) {
//             if(npc.hasTrait(WalletTrait.class)) {
//                  npc.getTrait(WalletTrait.class).deposit(getCost(player.getItemInHand()));
//              }
//            }
        }
        */

	private double getCost(ItemStack item, Player player) {
		DataKey root = config.getConfig().getKey("");
		double price = Setting.BASE_PRICE.asDouble();
		if (root.keyExists("base-prices." + item.getType().name().toLowerCase().replace('_', '-')))
			price = root.getDouble("base-prices." + item.getType().name().toLowerCase().replace('_', '-'));

		// Adjust price based on durability and enchantments


		if (root.keyExists("price-per-durability-point." + item.getType().name().toLowerCase().replace('_', '-')))
			price += item.getDurability() * root.getDouble("price-per-durability-point." + item.getType().name().toLowerCase().replace('_', '-'));
		else price += (item.getDurability() * Setting.PRICE_PER_DURABILITY_POINT.asDouble());


		double enchantmentModifier = Setting.ENCHANTMENT_MODIFIER.asDouble();
		for (Enchantment enchantment : item.getEnchantments().keySet()) {
			if (root.keyExists("enchantment-modifiers." + enchantment.getName().toLowerCase().replace('_', '-')))
				enchantmentModifier = root.getDouble("enchantment-modifiers."
						+ enchantment.getName().toLowerCase().replace('_', '-'));
			price += enchantmentModifier * item.getEnchantmentLevel(enchantment);
		}
		return price;
	}

	public static ItemStack itemStackFromSection(ConfigurationSection section){
		ItemStack is = new ItemStack(Material.valueOf(section.getString("item")));
		if(section.contains("amount"))
			is.setAmount(section.getInt("amount"));
		if(section.contains("meta")){
			ItemMeta im = is.getItemMeta();
			if(section.contains("meta.name"))
				im.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("meta.name")));
			if(section.contains("meta.lore")){
				List<String> lore = new ArrayList<>();
				section.getStringList("meta.lore").forEach(s -> lore.add(ChatColor.translateAlternateColorCodes('&', s)));
				im.setLore(lore);
			}
			is.setItemMeta(im);
		}
		return is;
	}
}
