package me.avixk.Notes;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Note {
    String message;
    String player;
    boolean anon;
    String time;
    ItemStack item;
    DateTimeFormatter formatter;
    public Note(String player, String message, boolean anon){// item is null if something is blacklisted
        this.player = player;
        this.message = message;
        this.anon = anon;
        try{
            formatter = DateTimeFormatter.ofPattern(Main.plugin.getConfig().getString("time_format"));
            time = ZonedDateTime.ofInstant(Instant.ofEpochSecond(System.currentTimeMillis() / 1000), ZoneOffset.UTC).format(formatter);
            message = Main.plugin.getConfig().getString("note_lore");
            message = replacePlaceholders(message);
            List<String> lore = new ArrayList<>();
            String color = ChatColor.getLastColors(message);
            for(String s : WordUtils.wrap(message,Main.plugin.getConfig().getInt("text_wrap_width"),"\n",true).split("\n")){
                lore.add(color + s);
            }
            lore.add(replacePlaceholders(Main.plugin.getConfig().getString("note_last_lore_line")));

            for(String s : Main.plugin.getConfig().getStringList("blacklisted_lore")){
                if(lore.contains(s)){
                    item = null;
                    return;
                }
            }

            String name = anon? Main.plugin.getConfig().getString("anon_title") : Main.plugin.getConfig().getString("note_name");

            item = createItem(Material.valueOf(Main.plugin.getConfig().getString("note_material")),
                    Integer.parseInt(Main.plugin.getConfig().getString("note_default_amount")),
                    replacePlaceholders(name),
                    lore);
        }catch (Exception e){
            item = createItem(Material.PAPER, 1, "§cError. Could not create your note. Malformed config?");
        }
    }

    public String replacePlaceholders(String text){
        return text
                .replace("\\n","\n")
                .replace("&","§")
                .replace("%message%",message)
                .replace("%player%",player)
                .replace("%time%",time);
    }


    public static ItemStack createItem(Material material) {
        return createItem(material, 1, null, null);
    }

    public static ItemStack createItem(Material material, int count) {
        return createItem(material, count, null, null);
    }

    public static ItemStack createItem(Material material, int count, String name) {
        return createItem(material, count, name, null);
    }

    public static ItemStack createItem(Material material, int count, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, count);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
        if (name != null) {
            meta.setDisplayName(name);
        }
        if (lore != null) {
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }
}
