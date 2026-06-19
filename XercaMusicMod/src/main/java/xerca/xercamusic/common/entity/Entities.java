package xerca.xercamusic.common.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import xerca.xercamusic.common.Mod;

public class Entities {
    private static final ResourceLocation MUSIC_SPIRIT_ID = Mod.id("music_spirit");
    private static final ResourceKey<EntityType<?>> MUSIC_SPIRIT_KEY = ResourceKey.create(Registries.ENTITY_TYPE, MUSIC_SPIRIT_ID);
    public static final EntityType<EntityMusicSpirit> MUSIC_SPIRIT = EntityType.Builder.<EntityMusicSpirit>of(EntityMusicSpirit::new, MobCategory.MISC)
            .sized(0.25f, 0.25f).updateInterval(10).build(MUSIC_SPIRIT_KEY);

    private Entities() {
    }

    public static void registerEntities() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, MUSIC_SPIRIT_ID, MUSIC_SPIRIT);
    }
}
