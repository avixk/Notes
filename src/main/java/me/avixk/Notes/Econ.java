package me.avixk.Notes;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;

public class Econ {
    public static Economy econ = null;
    public static boolean set_up = false;
    public static boolean setupEconomy() {
        if (Main.plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Main.plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        if(econ != null)set_up = true;
        return econ != null;
    }
    public static double getPlayerMoney(UUID player){
        return econ.getBalance(Bukkit.getOfflinePlayer(player));
    }
    public static boolean givePlayerMoney(UUID to, UUID from,double amount){
        OfflinePlayer pfrom = Bukkit.getOfflinePlayer(from);
        OfflinePlayer pto = Bukkit.getOfflinePlayer(to);
        double frombalance = econ.getBalance(pfrom);
        if(frombalance >= amount){
            EconomyResponse resultwith = econ.withdrawPlayer(pfrom, amount);
            if(resultwith.transactionSuccess()){
                EconomyResponse resultdep = econ.depositPlayer(pto, amount);
                if(resultdep.transactionSuccess()){
                    //eh
                }
                return true;
            }
        }
        return false;
    }
}