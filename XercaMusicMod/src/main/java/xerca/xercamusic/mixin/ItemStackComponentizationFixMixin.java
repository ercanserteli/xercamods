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

import static xerca.xercamusic.common.item.ItemMusicSheet.*;

@SuppressWarnings("unused")
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

            OptionalDynamic<?> id = itemStackData.removeTag(KEY_ID);
            id.get().ifSuccess((Dynamic<?> dynamic) -> {
                UUID sheetId = getUuidFromDynamic.apply(dynamic);
                if (sheetId != null) {
                    itemStackData.setComponent("xercamusic:sheet_id", tag.createString(sheetId.toString()));
                }
            });

            itemStackData.moveTagToComponent(KEY_GENERATION, "xercamusic:sheet_generation");
            itemStackData.moveTagToComponent(KEY_VERSION, "xercamusic:sheet_version");
            itemStackData.moveTagToComponent(KEY_LENGTH, "xercamusic:sheet_length");
            itemStackData.moveTagToComponent(KEY_BPS, "xercamusic:sheet_bps");
            itemStackData.moveTagToComponent(KEY_PREV_INSTRUMENT_LOCKED, "xercamusic:sheet_prev_instrument_locked");
            itemStackData.moveTagToComponent(KEY_PREV_INSTRUMENT, "xercamusic:sheet_prev_instrument");
            itemStackData.moveTagToComponent(KEY_HIGHLIGHT_INTERVAL, "xercamusic:sheet_highlight_interval");
            itemStackData.moveTagToComponent(KEY_VOLUME, "xercamusic:sheet_volume");
            itemStackData.moveTagToComponent(KEY_TITLE, "xercamusic:sheet_title");
            itemStackData.moveTagToComponent(KEY_AUTHOR, "xercamusic:sheet_author");
        }
    }
}
