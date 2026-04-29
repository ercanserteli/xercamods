package xerca.xercaconfetti;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Position;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercaconfetti.entity.EntityConfettiBall;
import xerca.xercaconfetti.item.ItemConfetti;
import xerca.xercaconfetti.item.ItemConfettiBall;
import xerca.xercaconfetti.packet.ConfettiParticlePacket;

import java.util.Collection;

public class Mod implements ModInitializer {

    public static final String MOD_ID = "xercaconfetti";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    // Item Definitions
    public static final ItemConfettiBall CONFETTI_BALL = new ItemConfettiBall();
    public static final ItemConfetti CONFETTI = new ItemConfetti();

    // EntityType Definitions
    public static final EntityType<EntityConfettiBall> ENTITY_CONFETTI_BALL = EntityType.Builder.<EntityConfettiBall>of(EntityConfettiBall::new, MobCategory.MISC)
            .sized(0.25f, 0.25f).updateInterval(10).build();

    // Sound Definitions
    public static final SoundEvent SOUND_CRACK = SoundEvent.createVariableRangeEvent(id("crack"));
    public static final SoundEvent SOUND_CONFETTI = SoundEvent.createVariableRangeEvent(id("confetti"));

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(ConfettiParticlePacket.PACKET_ID, ConfettiParticlePacket.PACKET_CODEC);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.accept(CONFETTI);
            entries.accept(CONFETTI_BALL);
        });

        // Entity Registration
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("confetti_ball"), ENTITY_CONFETTI_BALL);

        // Item Registration
        Registry.register(BuiltInRegistries.ITEM, id("confetti_ball"), CONFETTI_BALL);
        Registry.register(BuiltInRegistries.ITEM, id("confetti"), CONFETTI);

        // Sound Registration
        Registry.register(BuiltInRegistries.SOUND_EVENT, SOUND_CRACK.getLocation(), SOUND_CRACK);
        Registry.register(BuiltInRegistries.SOUND_EVENT, SOUND_CONFETTI.getLocation(), SOUND_CONFETTI);


        DispenserBlock.registerBehavior(CONFETTI_BALL, new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stackIn) {
                Position position = DispenserBlock.getDispensePosition(source);
                EntityConfettiBall projectile = new EntityConfettiBall(source.level(), position.x(), position.y(), position.z());
                projectile.setItem(stackIn.copyWithCount(1));
                projectile.shoot(source.state().getValue(DispenserBlock.FACING).getStepX(), source.state().getValue(DispenserBlock.FACING).getStepY() + 0.1F, source.state().getValue(DispenserBlock.FACING).getStepZ(), 1.1F, 6.0F);
                source.level().addFreshEntity(projectile);
                stackIn.shrink(1);
                return stackIn;
            }
        });
        DispenserBlock.registerBehavior(CONFETTI, new ConfettiDispenseItemBehavior());
        LOGGER.info(MOD_ID + " initialized");
    }

    public static void sendToClient(ServerPlayer player, ConfettiParticlePacket packet) {
        ServerPlayNetworking.send(player, packet);
    }

    public static void sendToClientsAround(ServerLevel level, Vec3 pos, double radius, ConfettiParticlePacket packet) {
        Collection<ServerPlayer> players = PlayerLookup.around(level, pos, radius);
        for (ServerPlayer player : players) {
            sendToClient(player, packet);
        }
    }
}
