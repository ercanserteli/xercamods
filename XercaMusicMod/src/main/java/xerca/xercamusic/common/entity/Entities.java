package xerca.xercamusic.common.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercamusic.common.Mod;

public class Entities {
    private static final Identifier MUSIC_SPIRIT_ID = Mod.id("music_spirit");
    private static final ResourceKey<EntityType<?>> MUSIC_SPIRIT_KEY = ResourceKey.create(Registries.ENTITY_TYPE, MUSIC_SPIRIT_ID);
    // Built during registration: EntityType.Builder.build writes to a frozen registry.
    public static EntityType<EntityMusicSpirit> MUSIC_SPIRIT;

    private Entities() {
    }

    public static void registerEntities(RegisterEvent.RegisterHelper<EntityType<?>> helper) {
        MUSIC_SPIRIT = EntityType.Builder.<EntityMusicSpirit>of(EntityMusicSpirit::new, MobCategory.MISC)
                .sized(0.25f, 0.25f).updateInterval(10).build(MUSIC_SPIRIT_KEY);
        helper.register(MUSIC_SPIRIT_ID, MUSIC_SPIRIT);
    }
}
