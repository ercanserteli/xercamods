package xerca.xercamusic.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xerca.xercamusic.common.XercaMusic;

public class Entities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, XercaMusic.MODID);

    public static final RegistryObject<EntityType<EntityMusicSpirit>> MUSIC_SPIRIT = ENTITIES.register("music_spirit", () -> EntityType.Builder.<EntityMusicSpirit>of(EntityMusicSpirit::new, MobCategory.MISC)
            .setCustomClientFactory(EntityMusicSpirit::new)
            .sized(0.25f, 0.25f).
            setUpdateInterval(10)
            .build("music_spirit")
    );
}
