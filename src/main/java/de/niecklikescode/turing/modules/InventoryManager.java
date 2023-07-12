package de.niecklikescode.turing.modules;

import de.niecklikescode.turing.api.events.UpdateEvent;
import de.niecklikescode.turing.api.modules.Info;
import de.niecklikescode.turing.api.modules.Module;
import de.niecklikescode.turing.api.utils.Inventory;
import de.niecklikescode.turing.api.utils.PlayerUtils;
import de.niecklikescode.turing.api.utils.Timer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import static de.niecklikescode.turing.api.utils.Inventory.*;

import java.util.*;
import java.util.stream.Collectors;

@Info(name = "Inventory Manager", description = "Sorts your inventory and throws crap away", category = Module.Category.PLAYER, keyBind = Keyboard.KEY_H)
public class InventoryManager extends Module {

    final Comparator<Slot> swordComparator = Comparator.comparingDouble(o -> getSwordValue(o.getStack()));

    private final Timer timer = new Timer();
    private final Random random = new Random();

    private final int DELAY = 110, DEVIATION = 20;

    final Inventory.Filter swordFilter = new Inventory.Filter() {
        @Override
        public boolean shouldSlotBeFiltered(ItemStack stack) {
            return stack.getItem() instanceof ItemSword;
        }

        @Override
        public List<Runnable> getActions() {
            ArrayList<Slot> swords = PlayerUtils.getItemSlots(stack -> stack.getItem() instanceof ItemSword);
            if(swords.isEmpty()) return Collections.emptyList();

            ArrayList<Runnable> actions = new ArrayList<>();

            swords.sort(swordComparator.reversed()); // Best sword will be at first place in list
            swords.remove(0); // Remove the best sword from list

            swords.forEach(slot -> {
                actions.add( dropItem(Inventory.inventoryToActionSlot(slot.getSlotIndex())));
            });

            return actions;
        }
    };

    // Limits the amount of blocks the player has in his inventory and throws away useless blocks
    final Filter blockFilter = new Filter() {
        @Override
        public boolean shouldSlotBeFiltered(ItemStack stack) {
            return stack.getItem() instanceof ItemBlock;
        }

        @Override
        public List<Runnable> getActions() {

           List<Slot> blocks = PlayerUtils.getItemSlots(stack -> stack.getItem() instanceof ItemBlock)
                   .stream()
                   .sorted(Comparator.comparingInt(slot -> slot.getStack().stackSize))
                   .collect(Collectors.toList()); // It would be annoying to switch stacks every 12 blocks when bridging, so we prioritize bigger stacks

            ArrayList<Runnable> actions = new ArrayList<>();

            int amount = 0;
            for (Slot slot : blocks) {
                if(!((ItemBlock)slot.getStack().getItem()).getBlock().isBlockNormalCube()) { // Blocks that are not normal cubes are next to useless so just throw them away right away
                    actions.add(dropItem(inventoryToActionSlot(slot.getSlotIndex())));
                    continue;
                }

                if(amount >= 3*64) actions.add(dropItem(inventoryToActionSlot(slot.getSlotIndex())));
                else amount += slot.getStack().stackSize;
            }

            return actions;
        }
    };

    // Keep only the best bow
    final Filter bowFilter = new Filter() {
        @Override
        public boolean shouldSlotBeFiltered(ItemStack stack) {
            return stack.getItem() == Items.bow;
        }

        @Override
        public List<Runnable> getActions() {
            ArrayList<Slot> bows = PlayerUtils.getItemSlots(stack -> stack.getItem() == Items.bow);
            bows.sort(Comparator.comparingDouble( (Slot slot) -> getBowValue(slot.getStack())).reversed()); // Best bow should be the first in the list, so reverse the comparator order

            if(bows.isEmpty()) return Collections.emptyList();
            bows.remove(0); // Only keep best bow

            ArrayList<Runnable> actions = new ArrayList<>();
            bows.forEach(bow -> actions.add(dropItem(inventoryToActionSlot(bow.getSlotIndex()))) );

            return actions;
        }
    };


    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if(!(MC.currentScreen instanceof GuiInventory)) return;

        ArrayList<Runnable> actions = getActions(swordFilter, blockFilter, bowFilter);
        if(actions.isEmpty()) return;

        Collections.shuffle(actions); // Shuffle actions to avoid anticheat detections

        long delay = (long)(DELAY+random.nextGaussian()*DEVIATION);
        timer.invokeIfComplete(delay, actions.get(0));
    }

    private ArrayList<Runnable> getActions(Inventory.Filter... filters) {
        ArrayList<Runnable> actions = new ArrayList<>();

        for (Slot slot : getLocalPlayer().inventoryContainer.inventorySlots) {
            if(slot.getStack() == null) continue;

            for (Inventory.Filter filter : filters) {
                if(filter.shouldSlotBeFiltered(slot.getStack())) filter.slots.add(slot);
                filter.slots.clear();
            }

        }

        for (Inventory.Filter filter : filters)
            actions.addAll(filter.getActions());

        return actions;
    }

    private Runnable dropItem(int slot) {
        return () -> {
            getLocalPlayer().inventoryContainer.slotClick(slot, 0, 4, getLocalPlayer()); // Update inventory client-side
            MC.playerController.windowClick(getLocalPlayer().inventoryContainer.windowId, slot, 1, 4, getLocalPlayer()); // Update inventory server-side
        };

    }

}
