package me.avixk.Notes;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
    static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
        if(!Econ.setupEconomy())Bukkit.getLogger().warning("Could not hook into Vault, note price will be disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("Only players can run this command.");
            return true;
        }
        Player p = (Player) sender;
        boolean anon = label.equalsIgnoreCase("anonnote");
        if(args.length == 0){
            return false;
        }

        if(!p.hasPermission(anon? "notes.use.anonymous" : "notes.use")){
            p.sendMessage("§cYou do not have permission to create notes.");
            return true;
        }

        double cost = 0;

        if(Econ.set_up){
            if(getConfig().getBoolean(anon? "cost.anonymous.enabled" : "cost.normal.enabled")){
                cost = getConfig().getDouble(anon? "cost.anonymous.amount" : "cost.normal.amount");
                if(Econ.getPlayerMoney(((Player) sender).getUniqueId()) < cost){
                    sender.sendMessage(getConfig().getString(anon? "messages.cant_afford_anonymous" : "messages.cant_afford_normal").replace("%cost%",cost+"").replace("&","§"));
                    return true;
                }
                EconomyResponse response = Econ.econ.withdrawPlayer(p,cost);
                if(!response.transactionSuccess()){
                    p.sendMessage("§cSomething went wrong, error message: " + response.errorMessage);
                    return true;
                }
            }
        }

        String message = "";
        for(String s : args){
            message += " " + s;
        }
        message = ChatColor.stripColor(message.replace("&","§"));
        Note note = new Note(sender.getName(),message,anon);
        if(note.item == null){
            sender.sendMessage(Main.plugin.getConfig().getString("blacklisted_lore_message").replace("&","§"));
            return true;
        }else{
            if(Util.getOverflow(p.getInventory().getContents(),note.item) < 0){
                p.getInventory().addItem(note.item);
                if(cost > 0){
                    p.sendMessage(getConfig().getString("messages.cost_nodrop").replace("%cost%",cost+"").replace("&","§"));
                }else{
                    p.sendMessage(getConfig().getString("messages.nocost_nodrop").replace("%cost%",cost+"").replace("&","§"));
                }
            }else{
                p.getWorld().dropItemNaturally(p.getLocation(),note.item);
                if(cost > 0){
                    p.sendMessage(getConfig().getString("messages.cost_drop").replace("%cost%",cost+"").replace("&","§"));
                }else{
                    p.sendMessage(getConfig().getString("messages.nocost_drop").replace("%cost%",cost+"").replace("&","§"));
                }
            }
        }
        return true;
    }
}
