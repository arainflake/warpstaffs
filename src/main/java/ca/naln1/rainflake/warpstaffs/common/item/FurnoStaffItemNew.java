package ca.naln1.rainflake.warpstaffs.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ca.naln1.rainflake.warpstaffs.common.StaffTypes;
import ca.naln1.rainflake.warpstaffs.gui.FurnaceStaffContainer;

public class FurnoStaffItemNew extends WarpStaffItem{
    public FurnoStaffItemNew(StaffTypes staff_type) {
        super(staff_type);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return null;
    }

    @Override
    public void isSneaking(PlayerEntity player, World world, ItemStack stack) {
        super.isSneaking(player, world, stack);
        if (world.isClientSide) return;
        INamedContainerProvider containerProvider = new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return ITextComponent.nullToEmpty("Furno Staff");
            }

            @Nullable
            @Override
            public Container createMenu(int container_id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
                return new FurnaceStaffContainer(container_id, player);
            }
        };
        NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider);
    }
}
