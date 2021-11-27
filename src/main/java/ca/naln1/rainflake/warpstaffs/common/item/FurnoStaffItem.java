package ca.naln1.rainflake.warpstaffs.common.item;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.UUID;

import ca.naln1.rainflake.warpstaffs.WarpStaffs;
import ca.naln1.rainflake.warpstaffs.common.StaffTypes;
import ca.naln1.rainflake.warpstaffs.network.FuelerPacket;

public class FurnoStaffItem extends WarpStaffItem implements WarpStaffItem.Fueler {
    private static final int BURN_TIME = 20 * 10;
    //private int ticks = 0;
    private int ticks = BURN_TIME;
    public boolean furno = false;
    public boolean found_fuel = false;

    public FurnoStaffItem(StaffTypes staff_type) {
        super(staff_type);
        setFueler(this);
        //CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.getDefaultInstance();
    }

    @Override
    public void isSneaking(PlayerEntity player, World world, ItemStack stack){
        if (world.isClientSide()) {
            furno = !furno;

            if (furno){
                player.sendMessage(ITextComponent.nullToEmpty("Furno Staff: enabled"), UUID.randomUUID());
                player.playSound(SoundEvents.SMOKER_SMOKE, 1, 1);
            }else {
                player.sendMessage(ITextComponent.nullToEmpty("Furno Staff: disabled"), UUID.randomUUID());
                player.playSound(SoundEvents.GENERIC_EXTINGUISH_FIRE, 1, 1);
                ticks = BURN_TIME;
            }
        }
        if (!furno && !player.isCreative()) {
            player.getCooldowns().addCooldown(stack.getItem(), 20*60);
        }
    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, World world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, world, entity, p_77663_4_, p_77663_5_);
        if (world.isClientSide()) {
            if (furno) {
                if (ticks >= BURN_TIME) {//every 10s
                    found_fuel = false;
                    if (!(entity instanceof PlayerEntity)) return;
                    PlayerEntity player = (PlayerEntity) entity;
                    if (!player.isCreative()) {
                        for (ItemStack itemStack : player.inventory.items) {
                            if (itemStack.getItem() == Items.COAL) {
                                found_fuel = true;
                                int slot = player.inventory.findSlotMatchingItem(itemStack);
                                player.inventory.removeItem(slot, 1);
                                WarpStaffs.network.sendToServer(new FuelerPacket(itemStack, slot));
                                break;
                            }
                        }
                    }else {
                        found_fuel = true;
                    }
                    ticks = 0;
                }
                ticks++;
                //particleSmoke(entity);
            }
        }
    }

    private void particleSmoke(Entity entity){
        if (found_fuel && (ticks % 4 == 0)) {
            PlayerEntity player = ((PlayerEntity) entity);
            Vector3d pos = player.position();
            Vector3d lpos = player.getLookAngle();
            float oset = -0.7f;
            //lpos = lpos.add(Math.abs(lpos.y) * lpos.xRot(oset).x, lpos.yRot(oset).y, Math.abs(lpos.y) * lpos.xRot(oset).z);
            //lpos = lpos.add(0, (1-Math.abs(lpos.y))*lpos.yRot(oset).y, 0);
            Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.SMOKE, pos.x + lpos.x, lpos.y + player.getEyeY(), pos.z + lpos.z, 0, 0, 0);
        }
    }

    @Override
    public boolean isFoil(ItemStack p_77636_1_) {
        return furno;
    }

    @Override
    public boolean hasFuel() {
        return furno && found_fuel;
    }
}
