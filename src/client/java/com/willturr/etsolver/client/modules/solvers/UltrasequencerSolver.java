package com.willturr.etsolver.client.modules.solvers;

import net.minecraft.block.PaneBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;
import java.util.List;

public class UltrasequencerSolver extends AbstractSolver{

    public UltrasequencerSolver(String containerName) {
        super(containerName);
    }

    MinecraftClient client = MinecraftClient.getInstance();

    //this could be achieved with a static list, maybe this is more resilient???
    List<Integer> clickStack = new ArrayList<>();
    int startSeconds = -1;

    //input delay tick variables
    int tickDelay = 3;
    int currentDelay = 0;

    @Override
    public void tick(GenericContainerScreen screen) {
        Inventory inventory = screen.getScreenHandler().getInventory();
        Item currentModeItem = inventory.getStack(49).getItem();

        if (currentModeItem == Items.GLOWSTONE) {
            startSeconds = -1;
            for (int i = 0; i < 45; i++) {
                var item = inventory.getStack(i).getItem();
                if (!(item instanceof BlockItem blockItem && blockItem.getBlock() instanceof PaneBlock)) {
                    //System.out.println(inventory.getStack(i).getItem().getName().getString());
                    if (inventory.getStack(i).getCount() == (clickStack.size() + 1)) {
                        clickStack.add(i); //add item index to stack
                        //System.out.println(clickStack);
                    }
                }
            }
        } else if (currentModeItem == Items.CLOCK) {
            if (startSeconds == -1) {
                startSeconds = inventory.getStack(49).getCount();
            }
            if (inventory.getStack(49).getCount() < startSeconds) {
                inputSequence(inventory, screen);
            }
        }
    }

    private void inputSequence(Inventory inventory, GenericContainerScreen screen) {
        //delay ticks
        if (currentDelay > 0) {
            currentDelay--;
            return;
        }


        if (client.player.currentScreenHandler.getCursorStack().getItem() == Items.AIR) {
            for (int i = 0; i < 45; i++) {
                if (!clickStack.isEmpty()) {
                    if (i == clickStack.getFirst()) {
                        ClientPlayerInteractionManager interactionManager = client.interactionManager;
                        //System.out.println("Clicking slot " + i);
                        interactionManager.clickSlot(
                                screen.getScreenHandler().syncId,
                                i,
                                0,
                                SlotActionType.PICKUP,
                                client.player
                        );
                        clickStack.removeFirst();
                        currentDelay = tickDelay;
                        break; //once item clicked, restart container search rather than continuing iterating
                    }
                }
            }
        }
    }

    //should prob bundle solver lists in AbstractSolver
    public void clearItemStack() {
        clickStack.clear();
    }
}
