package ca.naln1.rainflake.warpstaffs.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import ca.naln1.rainflake.warpstaffs.WarpStaffs;

public class FurnaceStaffScreen extends ContainerScreen<FurnaceStaffContainer> {

    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(WarpStaffs.MODID,"textures/gui/simple_gui.png");

    public FurnaceStaffScreen(FurnaceStaffContainer p_i51105_1_, PlayerInventory p_i51105_2_, ITextComponent p_i51105_3_) {
        super(p_i51105_1_, p_i51105_2_, p_i51105_3_);
        //this.passEvents = false;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(CONTAINER_BACKGROUND);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        //matrixStack, gui-x, gui-y, u, v, u-width, v-width
        //this.getMenu().itemStack;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.renderBackground(p_230430_1_);
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);//slot highlighting
        this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
    }
}
