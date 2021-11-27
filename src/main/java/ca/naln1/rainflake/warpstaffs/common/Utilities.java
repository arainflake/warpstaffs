package ca.naln1.rainflake.warpstaffs.common;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.common.world.ForgeChunkManager;

import ca.naln1.rainflake.warpstaffs.WarpStaffs;
import ca.naln1.rainflake.warpstaffs.common.item.WarpStaffItem;

public class Utilities {
    public static int vecDist(Vector3d from, Vector3d to){
        //returns the euclidean distance
        double x = from.x;
        double y = from.y;
        double z = from.z;
        double x1 = to.x;
        double y1 = to.y;
        double z1 = to.z;
        return (int) Math.ceil(Math.sqrt(Math.pow(x-x1, 2) + Math.pow(y-y1,2) + Math.pow(z-z1,2)));
    }

    public static void ensureChunk(World world, PlayerEntity player, ChunkPos chunkPos){
        if (!world.isClientSide) {
            ServerWorld serverWorld = ((ServerWorld) world);
            if (!world.hasChunk(chunkPos.x, chunkPos.z)) {
                ForgeChunkManager.forceChunk(serverWorld, WarpStaffs.MODID, player, chunkPos.x, chunkPos.z, true, false);

            }
        }
    }

    public static boolean canStandBelow(Vector3d from, World world, PlayerEntity player){
        Vector3d from2 = new Vector3d(Math.floor(from.x), Math.floor(from.y), Math.floor(from.z));
        BlockRayTraceResult pos = world.clip(new RayTraceContext(from2, new Vector3d(from2.x,from2.y-1,from2.z), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player));
        return from2.y == pos.getLocation().y + 1;
    }

    public static boolean canStandAtPos(double x, double y, double z, World world, PlayerEntity player){
        BlockRayTraceResult pos = world.clip(new RayTraceContext(new Vector3d(x,y,z), new Vector3d(x,y+2,z), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player));
        return y == pos.getLocation().y - 2;
    }

    public static boolean canStandAtPos(Vector3d pos, World world, PlayerEntity player){

        BlockRayTraceResult temp = world.clip(new RayTraceContext(pos, new Vector3d(pos.x,pos.y+2, pos.z), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player));
        return pos.y == temp.getLocation().y - 2;
    }

    public static BlockRayTraceResult getNearestBlockCollided(World world, PlayerEntity player, int distance) {
        double d0 = player.getX();
        //double d1 = player.getY();
        double d1 = player.getEyeY();
        double d2 = player.getZ();
        Vector3d vec_from = new Vector3d(d0, d1, d2);

        Vector3d look = player.getLookAngle();
        double offX = look.x * distance;
        double offY = look.y * distance;
        double offZ = look.z * distance;

        Vector3d vec_to = new Vector3d(d0 + offX, d1 + offY, d2 + offZ);
        return world.clip(new RayTraceContext(vec_from, vec_to, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player));
        //return getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.ANY);
    }

    public static void warpDestinationNew(RenderHelper event){

        PlayerEntity player = Minecraft.getInstance().player;
        if (!(player.getMainHandItem().getItem() instanceof WarpStaffItem)) return;

        BlockRayTraceResult rtr = getNearestBlockCollided(player.level, player, 64);
        float colorR = 1, colorG = 0, colorB = 0;

        MatrixStack ms = new MatrixStack();
        IRenderTypeBuffer buffer = IRenderTypeBuffer.immediate(new BufferBuilder(8));
        IVertexBuilder lineBuilder = buffer.getBuffer(RenderType.lines());

        double partialTicks = 0;//event.getPartialTicks();
        double d0 = player.xOld + (player.getX() - player.xOld) * partialTicks;
        double d1 = player.yOld + player.getEyeHeight() + (player.getY() - player.yOld) * partialTicks;
        double d2 = player.zOld + (player.getZ() - player.zOld) * partialTicks;

        int side = rtr.getDirection().ordinal();
        d0 = d0 + (side == 4 ? 1 : 0) - (side == 5 ? 1 : 0);
        d1 = d1 + (side == 0 ? 1 : 0) - (side == 1 ? 1 : 0);
        d2 = d2 + (side == 2 ? 1 : 0) - (side == 3 ? 1 : 0);

        ms.pushPose();
        AxisAlignedBB aabb = new AxisAlignedBB(rtr.getBlockPos()).move(-d0, -d1, -d2);
        WorldRenderer.renderLineBox(ms, lineBuilder, aabb, colorR, colorG, colorB, 0.4F);
        ms.popPose();

        //event.setCanceled(true);
    }

    public static void warpDestination(DrawHighlightEvent event){
        if(event.getTarget().getType() != RayTraceResult.Type.BLOCK) return;

        BlockRayTraceResult rtr = (BlockRayTraceResult) event.getTarget();
        Entity entity = event.getInfo().getEntity();
        if(!(entity instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) entity;
        if (!(player.getMainHandItem().getItem() instanceof WarpStaffItem)) return;
        float colorR = 1, colorG = 0, colorB = 0;

        MatrixStack ms = event.getMatrix();
        IRenderTypeBuffer buffer = event.getBuffers();
        IVertexBuilder lineBuilder = buffer.getBuffer(RenderType.lines());

        double partialTicks = event.getPartialTicks();
        double d0 = player.xOld + (player.getX() - player.xOld) * partialTicks;
        double d1 = player.yOld + player.getEyeHeight() + (player.getY() - player.yOld) * partialTicks;
        double d2 = player.zOld + (player.getZ() - player.zOld) * partialTicks;

        int side = rtr.getDirection().ordinal();
        d0 = d0 + (side == 4 ? 1 : 0) - (side == 5 ? 1 : 0);
        d1 = d1 + (side == 0 ? 1 : 0) - (side == 1 ? 1 : 0);
        d2 = d2 + (side == 2 ? 1 : 0) - (side == 3 ? 1 : 0);

        ms.pushPose();
        AxisAlignedBB aabb = new AxisAlignedBB(rtr.getBlockPos()).move(-d0, -d1, -d2);
        WorldRenderer.renderLineBox(ms, lineBuilder, aabb, colorR, colorG, colorB, 0.4F);
        ms.popPose();

        event.setCanceled(true);
    }
}
