package ca.naln1.rainflake.warpstaffs.network;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class FuelerPacket {
    private final ItemStack itemStack;
    private final int slotIndex;

    public FuelerPacket(ItemStack itemStack, int slotIndex) {
        this.itemStack = itemStack;
        this.slotIndex = slotIndex;
    }

    public FuelerPacket(PacketBuffer buf){
        this.itemStack = buf.readItem();
        this.slotIndex = buf.readInt();
    }

    public void toBytes(PacketBuffer buf){
        buf.writeItem(this.itemStack);
        buf.writeInt(this.slotIndex);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            ctx.get().getSender().inventory.setItem(this.slotIndex, this.itemStack);
        });
        ctx.get().setPacketHandled(true);
    }
}
