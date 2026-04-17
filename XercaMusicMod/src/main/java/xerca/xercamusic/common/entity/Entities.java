package xerca.xercamusic.common.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import xerca.xercamusic.common.Mod;

public class Entities {
    public static final EntityType<EntityMusicSpirit> MUSIC_SPIRIT = EntityType.Builder.<EntityMusicSpirit>of(EntityMusicSpirit::new, MobCategory.MISC)
            .sized(0.25f, 0.25f).updateInterval(10).build();

    private Entities() {
    }

    public static void registerEntities() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, Mod.id("music_spirit"), MUSIC_SPIRIT);
    }
}
