package me.avixk.Notes;

import org.bukkit.inventory.ItemStack;

public class Util {

    public static int getOverflow(ItemStack[] contents, ItemStack targetItem){
        //Util.log("Item");
        int count = targetItem.getAmount();
        //Util.log("Count: " + count);
        for(ItemStack item : contents.clone()){
            if(item == null) {
                count -= targetItem.getType().getMaxStackSize();
            }else if(item.isSimilar(targetItem.clone())){
                //Util.log(" ! Is Similar !");
                // Util.log("  " + count + "-" + item.getMaxStackSize() + "-" + item.getAmount() + "");
                count = count - (item.getMaxStackSize() - item.getAmount());
                // Util.log("  = " + count);
            }
        }
        //Util.log("   FINAL COUNT: " + count);
        return count;
    }
}
