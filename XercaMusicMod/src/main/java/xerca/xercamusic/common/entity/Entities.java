package xerca.xercamusic.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercamusic.common.Mod;

public class Entities {
    // EntityType.Builder.build writes to a registry, so build during registration.
    public static EntityType<EntityMusicSpirit> MUSIC_SPIRIT;

    private Entities() {
    }

    public static void registerEntities(RegisterEvent.RegisterHelper<EntityType<?>> helper) {
        MUSIC_SPIRIT = EntityType.Builder.<EntityMusicSpirit>of(EntityMusicSpirit::new, MobCategory.MISC)
                .sized(0.25f, 0.25f).updateInterval(10).build("music_spirit");
        helper.register(Mod.id("music_spirit"), MUSIC_SPIRIT);
    }
}
