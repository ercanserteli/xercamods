package xerca.xercaconfetti;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Position;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercaconfetti.entity.EntityConfettiBall;
import xerca.xercaconfetti.item.ItemConfetti;
import xerca.xercaconfetti.item.ItemConfettiBall;
import xerca.xercaconfetti.packet.IPacket;

import java.util.Collection;

public class Mod implements ModInitializer {

    public static final String modId = "xercaconfetti";
    public static final Logger LOGGER = LogManager.getLogger(modId);


    // Item Definitions
    public static final ItemConfettiBall ITEM_CONFETTI_BALL = new ItemConfettiBall();
    public static final ItemConfetti ITEM_CONFETTI = new ItemConfetti();


    // EntityType Definitions
    public static final EntityType<EntityConfettiBall> ENTITY_CONFETTI_BALL = FabricEntityTypeBuilder.<EntityConfettiBall>create(MobCategory.MISC, EntityConfettiBall::new)
            .dimensions(new EntityDimensions(0.25f, 0.25f, true)).trackedUpdateRate(10).build();


    // Sound Definitions
    public final static SoundEvent SOUND_CRACK = SoundEvent.createVariableRangeEvent(new ResourceLocation(Mod.modId, "crack"));
    public final static SoundEvent SOUND_CONFETTI = SoundEvent.createVariableRangeEvent(new ResourceLocation(Mod.modId, "confetti"));

    @Override
    public void onInitialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.accept(ITEM_CONFETTI);
            entries.accept(ITEM_CONFETTI_BALL);
        });

        // Entity Registration
        Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(modId, "confetti_ball"), ENTITY_CONFETTI_BALL);

        // Item Registration
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(modId, "confetti_ball"), ITEM_CONFETTI_BALL);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(modId, "confetti"), ITEM_CONFETTI);

        // Sound Registration
        Registry.register(BuiltInRegistries.SOUND_EVENT, SOUND_CRACK.getLocation(), SOUND_CRACK);
        Registry.register(BuiltInRegistries.SOUND_EVENT, SOUND_CONFETTI.getLocation(), SOUND_CONFETTI);


        DispenserBlock.registerBehavior(ITEM_CONFETTI_BALL, new AbstractProjectileDispenseBehavior()
        {
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn)
            {
                return new EntityConfettiBall(worldIn, position.x(), position.y(), position.z());
            }
        });
        DispenserBlock.registerBehavior(ITEM_CONFETTI, new ConfettiDispenseItemBehavior());
    }

    public static void sendToClient(ServerPlayer player, IPacket packet) {
        ServerPlayNetworking.send(player, packet.getID(), packet.encode());
    }

    public static void sendToClientsAround(ServerLevel level, Vec3 pos, double radius, IPacket packet) {
        Collection<ServerPlayer> players = PlayerLookup.around(level, pos, radius);
        for(ServerPlayer player : players) {
            sendToClient(player, packet);
        }
    }
}
