package xerca.xercamusic.common.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import xerca.xercamusic.common.Mod;

public class Entities {
    public static final EntityType<EntityMusicSpirit> MUSIC_SPIRIT = FabricEntityTypeBuilder.<EntityMusicSpirit>create(MobCategory.MISC, EntityMusicSpirit::new)
            .dimensions(new EntityDimensions(0.25f, 0.25f, 0.25f, EntityAttachments.createDefault(0.25f, 0.25f),true)).trackedUpdateRate(10).build();

    public static void registerEntities() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, Mod.id("music_spirit"), MUSIC_SPIRIT);
    }
}
