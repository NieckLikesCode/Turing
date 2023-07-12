package de.niecklikescode.turing.api.utils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class Inventory {

    public static double getSwordValue(ItemStack stack) {

        ItemSword sword = (ItemSword) stack.getItem();
        double value = 4 + sword.getDamageVsEntity();

        // Account for sharpness
        value += 0.5 * (EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) - 1) + 1.0;

        // This isn't the actual formula to calculate the value taken from fire aspect, but I feel as if
        // this way is closer to how you would actually choose what sword to use
        value += 0.25 * EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 4;

        // Account for weapon durability
        value += 1 * (float) (stack.getMaxDamage() - stack.getItemDamage()) / stack.getMaxDamage();

        return value;
    }

    public static double getBowValue(ItemStack stack) {

        ItemBow bow = (ItemBow) stack.getItem();
        double value = 1;

        // Account for power
        value *= EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);

        // Infinity is only really important when we don't have enough arrows in our inventory
        if(getItemAmount(arrow -> arrow.getItem() == Items.arrow) < 64) value += EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack);

        // Account for weapon durability
        value += 1 * (float) (stack.getMaxDamage() - stack.getItemDamage()) / stack.getMaxDamage();

        return value;
    }

    public static int getItemAmount(Predicate<ItemStack> predicate) {
        AtomicInteger amount = new AtomicInteger();
        PlayerUtils.getItemSlots(predicate).forEach(slot -> amount.addAndGet(slot.getStack().stackSize));
        return amount.get();
    }

    public static int inventoryToActionSlot(int slot) {
        if(slot >= 0 && slot <= 8) slot += 36;
        return slot;
    }

    public interface Filter {


        /**
         * Holds the slots that should be filtered
         */
        ArrayList<Slot> slots = new ArrayList<>();

        /**
         * @param stack Stack that might get filtered
         * @return Whether a slot fits to a filter or not
         */
        boolean shouldSlotBeFiltered(ItemStack stack);

        /**
         * @return A list of actions that should be performed
         */
        List<Runnable> getActions();

    }

}
