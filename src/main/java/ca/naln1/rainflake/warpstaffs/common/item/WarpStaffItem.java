package ca.naln1.rainflake.warpstaffs.common.item;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import ca.naln1.rainflake.warpstaffs.WarpStaffs;
import ca.naln1.rainflake.warpstaffs.common.ConfigHandler;
import ca.naln1.rainflake.warpstaffs.common.StaffTypes;

import static ca.naln1.rainflake.warpstaffs.common.Utilities.getNearestBlockCollided;
import static ca.naln1.rainflake.warpstaffs.common.Utilities.vecDist;

public class WarpStaffItem extends Item {
    private Fueler fueler;

    public Inventory inventory;
    public final int distance;
    public final int cool_down;
    public final int use_dmg;
    public final int base_dmg = 10;
    public StaffTypes staff_type;

    public WarpStaffItem(StaffTypes staff_type) {
        super(new Item.Properties().durability(1000).rarity(staff_type.getRarity()).tab(WarpStaffs.warp_group));
        this.staff_type = staff_type;
        this.distance = staff_type.getDistance();
        this.cool_down = ((int) Math.ceil(staff_type.getCool_down() * 20));
        this.use_dmg = staff_type.getUse_dmg();
        this.inventory = new Inventory(1);
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> use(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        //if (!world.isClientSide()) {
            if (hand.equals(Hand.MAIN_HAND)) {
                if (!player.isCrouching()) {
                    if (!player.getCooldowns().isOnCooldown(stack.getItem())) {

                        //whatevers smaller, staff distance, or chunk render distance
                        BlockRayTraceResult pos = getNearestBlockCollided(world, player, Math.min(distance, ((int) Minecraft.getInstance().gameRenderer.getRenderDistance())));
                        if (pos != null) {
                            int side = pos.getDirection().ordinal();
                            if (side != -1) {

                                double x = pos.getLocation().x - (side == 4 ? 0.31 : 0) + (side == 5 ? 0.31 : 0);
                                double y = pos.getLocation().y - (side == 0 ? 1.81 : 0) + (side == 1 ? 0.0 : 0);
                                double z = pos.getLocation().z - (side == 2 ? 0.31 : 0) + (side == 3 ? 0.31 : 0);

                                //so position doesnt get canceled from being in corners, center in block
                                /*
                                x = Math.floor(x) + 0.5;
                                y = Math.floor(y);
                                z = Math.floor(z) + 0.5;

                                 */
                                boolean canWarp = true;
                                //boolean canWarp = true;

                                /*
                                if (canStandAtPos(x, y, z, world, player)) {
                                    canWarp = true;
                                }else if (canStandBelow(new Vector3d(x,y,z), world, player)){
                                    y-=1;
                                    canWarp = true;
                                }

                                 */
                                if (fueler == null || fueler.hasFuel()) {
                                    if (canWarp) {
                                        double travel_dist = vecDist(player.position(), new Vector3d(x, y, z));

                                        if (displayMsg(player)) {
                                            player.displayClientMessage(new StringTextComponent("travel_dist:" + travel_dist), true);
                                        }
                                        player.fallDistance = 0;
                                        //force load chunk?
                                        //player.checkAndResetForcedChunkAdditionFlag()
                                        player.moveTo(x, y, z);
                                        player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1, 1);

                                        if (!player.isCreative()) {
                                            if (ConfigHandler.shouldDamagePlayer.get()) {
                                                player.hurt(DamageSource.GENERIC, staff_type.getPlayer_dmg());
                                            }
                                            player.getCooldowns().addCooldown(stack.getItem(), cool_down);
                                            setDamage(stack, (int) (getDamage(stack) + Math.ceil(use_dmg * (travel_dist / distance))));
                                        }

                                        if (getDamage(stack) >= getMaxDamage(stack)) {
                                            player.swing(hand);
                                            player.playSound(SoundEvents.ITEM_BREAK, 1, 1);
                                            return ActionResult.success(ItemStack.EMPTY);
                                        } else {
                                            return ActionResult.success(stack);
                                        }
                                    } else {
                                        player.displayClientMessage(new StringTextComponent("Obstructed"), true);
                                    }
                                }else {
                                    player.displayClientMessage(new StringTextComponent("No Fuel"), true);
                                }
                            }
                        }
                    }
                } else {//is sneaking
                    isSneaking(player, world, stack);
                }//is sneaking
            }//is in main hand
        //}
        player.swing(hand);
        return ActionResult.pass(stack);
    }

    //public doWarp()

    public boolean displayMsg(PlayerEntity player){
        CompoundNBT nbt = player.serializeNBT();
        return !nbt.contains("distTraveled") || nbt.getBoolean("distTraveled");
    }

    @Override
    public boolean isEnchantable(ItemStack itemStack) {
        for (int x = 0; x < itemStack.getEnchantmentTags().size(); x++) {
            if (itemStack.getEnchantmentTags().get(x) ==
                    EnchantedBookItem.createForEnchantment(
                            new EnchantmentData(Enchantments.FALL_PROTECTION, 1))
                            .getEnchantmentTags()) {
                return false;
            }
        }
        return false;
        //return super.isEnchantable(itemStack);
    }

    @Override
    public boolean isRepairable(@Nonnull ItemStack stack) {
        return super.isRepairable(stack);
    }

    public void isSneaking(PlayerEntity player, World world, ItemStack stack) {
        /*
        CompoundNBT nbt = player.serializeNBT();
        nbt.putBoolean("distTraveled", false);
        player.deserializeNBT(nbt);
        WarpStaffs.LOGGER.info(nbt.getAllKeys().toString());

         */
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }

    public void setFueler(Fueler fueler){
        this.fueler = fueler;
    }

    public interface Fueler{
        boolean hasFuel();
    }
}
