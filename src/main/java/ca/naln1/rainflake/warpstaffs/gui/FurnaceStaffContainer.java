package ca.naln1.rainflake.warpstaffs.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class FurnaceStaffContainer extends Container implements ICapabilityProvider, IItemHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    public ItemStack itemStack;
    @CapabilityInject(IItemHandler.class)
    public static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;
    /*
    public static FurnaceStaffContainer create(int windowID, PlayerInventory playerInventory, PacketBuffer data){
        return new FurnaceStaffContainer(windowID, playerInventory.player);
    }

     */

    public FurnaceStaffContainer(int container_id, PlayerEntity player) {
        super(WarpStaffsContainerTypes.FURNO_STAFF.get(), container_id);
        PlayerInventory playerInventory = player.inventory;

        //itemStack = player.getMainHandItem();
        //inventory.startOpen(playerInventory.player);

        //furnostaff slot
        /*
        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            addSlot(new SlotItemHandler(h, 0, 80,35));
        });
         */
        addSlot(new Slot(playerInventory, 0, 80,35));

        //hotbar slots
        for(int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        //inventory slots
        for(int row = 0; row < 3; ++row) {
            for(int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity playerEntity) {
        return true;
        //return this.stillValid(playerEntity);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        return (LazyOptional<T>) cap.getDefaultInstance();
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return null;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return null;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return null;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return false;
    }
}
