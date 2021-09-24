package xerca.xercamod.client;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.XercaMod;

@OnlyIn(Dist.CLIENT)
public class OmniChestTileEntityRenderer<T extends BlockEntity & LidBlockEntity> extends ChestRenderer<T> {
    public static final ResourceLocation texture = new ResourceLocation(XercaMod.MODID, "block/omni_chest");
    public static final Material material = new Material(Sheets.CHEST_SHEET, texture);

    public OmniChestTileEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected Material getMaterial(T blockEntity, ChestType chestType) {
        return material;
    }
}
