package xerca.xercamusic.mixin;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xerca.xercamusic.common.Mod;

import java.util.UUID;
import java.util.function.Function;

@Mixin(ItemStackComponentizationFix.class)
public class ItemStackComponentizationFixMixin {
    @Inject(at = @At("TAIL"), method = "fixItemStack(Lnet/minecraft/util/datafix/fixes/ItemStackComponentizationFix$ItemStackData;Lcom/mojang/serialization/Dynamic;)V")
    private static void fixItemStackMixin(ItemStackComponentizationFix.ItemStackData itemStackData, Dynamic<?> tag, CallbackInfo info) {
        if (itemStackData.is("xercamusic:music_sheet")) {
            Function<Dynamic<?>, UUID> getUuidFromDynamic = (Dynamic<?> dynamic) -> {
                if (dynamic.getOps() == NbtOps.INSTANCE) {
                    Tag nbtElement = (Tag) dynamic.getValue();
                    if (nbtElement instanceof IntArrayTag) {
                        return NbtUtils.loadUUID(nbtElement);
                    }
                }
                return null;
            };

            Mod.LOGGER.debug("Found a music sheet, porting it to the component format");

            OptionalDynamic<?> id = itemStackData.removeTag("id");
            id.get().ifSuccess((Dynamic<?> dynamic) -> {
                UUID sheetId = getUuidFromDynamic.apply(dynamic);
                if (sheetId != null) {
                    itemStackData.setComponent("xercamusic:sheet_id", tag.createString(sheetId.toString()));
                }
            });

            itemStackData.moveTagToComponent("generation", "xercamusic:sheet_generation");
            itemStackData.moveTagToComponent("ver", "xercamusic:sheet_version");
            itemStackData.moveTagToComponent("l", "xercamusic:sheet_length");
            itemStackData.moveTagToComponent("bps", "xercamusic:sheet_bps");
            itemStackData.moveTagToComponent("piLocked", "xercamusic:sheet_prev_instrument_locked");
            itemStackData.moveTagToComponent("prevIns", "xercamusic:sheet_prev_instrument");
            itemStackData.moveTagToComponent("hl", "xercamusic:sheet_highlight_interval");
            itemStackData.moveTagToComponent("vol", "xercamusic:sheet_volume");
            itemStackData.moveTagToComponent("title", "xercamusic:sheet_title");
            itemStackData.moveTagToComponent("author", "xercamusic:sheet_author");
        }
    }
}
