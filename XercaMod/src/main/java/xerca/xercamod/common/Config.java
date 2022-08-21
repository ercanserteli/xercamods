package xerca.xercamod.common;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import xerca.xercamod.common.packets.ConfigSyncPacket;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = XercaMod.MODID)
public class Config {
    public static final Map<String, Supplier<Boolean>> conditionMap;
    static {
        conditionMap = new HashMap<>(16);
        conditionMap.put("grab_hook", Config::isGrabHookEnabled);
        conditionMap.put("warhammer", Config::isWarhammerEnabled);
        conditionMap.put("cushion", Config::isCushionEnabled);
        conditionMap.put("tea", Config::isTeaEnabled);
        conditionMap.put("food", Config::isFoodEnabled);
        conditionMap.put("confetti", Config::isConfettiEnabled);
        conditionMap.put("ender_flask", Config::isEnderFlaskEnabled);
        conditionMap.put("courtroom", Config::isCourtroomEnabled);
        conditionMap.put("carved_wood", Config::isCarvedWoodEnabled);
        conditionMap.put("leather_straw", Config::isLeatherStrawEnabled);
        conditionMap.put("bookcase", Config::isBookcaseEnabled);
        conditionMap.put("coins", Config::isCoinsEnabled);
        conditionMap.put("scythe", Config::isScytheEnabled);
        conditionMap.put("spyglass", Config::isSpyglassEnabled);
        conditionMap.put("rope", Config::isRopeEnabled);
        conditionMap.put("terracotta_tile", Config::isTerracottaTileEnabled);
        conditionMap.put("omni_chest", Config::isOmniChestEnabled);
    }

    public static final String CATEGORY_GENERAL = "general";

    private static final ForgeConfigSpec.Builder COMMON_BUILD = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec COMMON_CONFIG;

    private static final ForgeConfigSpec.BooleanValue GRAB_HOOK_ENABLE;
    private static final ForgeConfigSpec.BooleanValue WARHAMMER_ENABLE;
    private static final ForgeConfigSpec.BooleanValue CUSHION_ENABLE;
    private static final ForgeConfigSpec.BooleanValue TEA_ENABLE;
    private static final ForgeConfigSpec.BooleanValue FOOD_ENABLE;
    private static final ForgeConfigSpec.BooleanValue CONFETTI_ENABLE;
    private static final ForgeConfigSpec.BooleanValue ENDER_FLASK_ENABLE;
    private static final ForgeConfigSpec.BooleanValue COURTROOM_ENABLE;
    private static final ForgeConfigSpec.BooleanValue CARVED_WOOD_ENABLE;
    private static final ForgeConfigSpec.BooleanValue LEATHER_STRAW_ENABLE;
    private static final ForgeConfigSpec.BooleanValue BOOKCASE_ENABLE;
    private static final ForgeConfigSpec.BooleanValue COINS_ENABLE;
    private static final ForgeConfigSpec.BooleanValue SCYTHE_ENABLE;
    private static final ForgeConfigSpec.BooleanValue SPYGLASS_ENABLE;
    private static final ForgeConfigSpec.BooleanValue ROPE_ENABLE;
    private static final ForgeConfigSpec.BooleanValue TERRACOTTA_TILE_ENABLE;
    private static final ForgeConfigSpec.BooleanValue OMNI_CHEST_ENABLE;

    private static boolean grabHookEnabled;
    private static boolean warhammerEnabled;
    private static boolean cushionEnabled;
    private static boolean teaEnabled;
    private static boolean foodEnabled;
    private static boolean confettiEnabled;
    private static boolean enderFlaskEnabled;
    private static boolean courtroomEnabled;
    private static boolean carvedWoodEnabled;
    private static boolean leatherStrawEnabled;
    private static boolean bookcaseEnabled;
    private static boolean coinsEnabled;
    private static boolean scytheEnabled;
    private static boolean spyglassEnabled;
    private static boolean ropeEnabled;
    private static boolean terracottaTileEnabled;
    private static boolean omniChestEnabled;

    static {

        COMMON_BUILD.comment("General settings").push(CATEGORY_GENERAL);

        GRAB_HOOK_ENABLE = COMMON_BUILD.comment("Enable Grab Hook").define("grab_hook", true);
        WARHAMMER_ENABLE = COMMON_BUILD.comment("Enable Warhammer").define("warhammer", true);
        CUSHION_ENABLE = COMMON_BUILD.comment("Enable Cushion").define("cushion", true);
        TEA_ENABLE = COMMON_BUILD.comment("Enable Tea").define("tea", true);
        FOOD_ENABLE = COMMON_BUILD.comment("Enable Food").define("food", true);
        CONFETTI_ENABLE = COMMON_BUILD.comment("Enable Confetti").define("confetti", true);
        ENDER_FLASK_ENABLE = COMMON_BUILD.comment("Enable Ender Flask").define("flask", true);
        COURTROOM_ENABLE = COMMON_BUILD.comment("Enable Courtroom Items").define("courtroom", true);
        CARVED_WOOD_ENABLE = COMMON_BUILD.comment("Enable Carved Wood").define("carved_wood", true);
        LEATHER_STRAW_ENABLE = COMMON_BUILD.comment("Enable Leather and Straw Blocks").define("leather_straw", true);
        BOOKCASE_ENABLE = COMMON_BUILD.comment("Enable Bookcase").define("bookcase", true);
        COINS_ENABLE = COMMON_BUILD.comment("Enable Golden Coins").define("coins", true);
        SCYTHE_ENABLE = COMMON_BUILD.comment("Enable Scythe").define("scythe", true);
        SPYGLASS_ENABLE = COMMON_BUILD.comment("Enable Spyglass").define("spyglass", true);
        ROPE_ENABLE = COMMON_BUILD.comment("Enable Rope").define("rope", true);
        TERRACOTTA_TILE_ENABLE = COMMON_BUILD.comment("Enable Terracotta Tiles").define("terracotta_tile", true);
        OMNI_CHEST_ENABLE = COMMON_BUILD.comment("Enable Omni Chest").define("omni_chest", true);

        COMMON_BUILD.pop();

        COMMON_CONFIG = COMMON_BUILD.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {

        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);

        bakeConfig();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        XercaMod.LOGGER.debug("Config load event");
        Config.bakeConfig();
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading configEvent) {
        XercaMod.LOGGER.debug("Config reload event");
        Config.bakeConfig();
    }

    public static void bakeConfig(){
        grabHookEnabled = GRAB_HOOK_ENABLE.get();
        warhammerEnabled = WARHAMMER_ENABLE.get();
        cushionEnabled = CUSHION_ENABLE.get();
        teaEnabled = TEA_ENABLE.get();
        foodEnabled = FOOD_ENABLE.get();
        confettiEnabled = CONFETTI_ENABLE.get();
        enderFlaskEnabled = ENDER_FLASK_ENABLE.get();
        courtroomEnabled = COURTROOM_ENABLE.get();
        carvedWoodEnabled = CARVED_WOOD_ENABLE.get();
        leatherStrawEnabled = LEATHER_STRAW_ENABLE.get();
        bookcaseEnabled = BOOKCASE_ENABLE.get();
        coinsEnabled = COINS_ENABLE.get();
        scytheEnabled = SCYTHE_ENABLE.get();
        spyglassEnabled = SPYGLASS_ENABLE.get();
        ropeEnabled = ROPE_ENABLE.get();
        terracottaTileEnabled = TERRACOTTA_TILE_ENABLE.get();
        omniChestEnabled = OMNI_CHEST_ENABLE.get();
    }

    public static void syncWithPacket(ConfigSyncPacket packet){
        if(packet.isMessageValid()){
            grabHookEnabled =  packet.grabHook;
            warhammerEnabled =  packet.warhammer;
            cushionEnabled =  packet.cushion;
            teaEnabled =  packet.tea;
            foodEnabled =  packet.food;
            confettiEnabled =  packet.confetti;
            enderFlaskEnabled =  packet.enderFlask;
            courtroomEnabled =  packet.courtroom;
            carvedWoodEnabled =  packet.carvedWood;
            leatherStrawEnabled =  packet.leatherStraw;
            bookcaseEnabled =  packet.bookcase;
            coinsEnabled =  packet.coins;
            scytheEnabled =  packet.scythe;
            spyglassEnabled =  packet.spyglass;
            ropeEnabled =  packet.rope;
            terracottaTileEnabled =  packet.terracottaTile;
            omniChestEnabled =  packet.omniChest;
        }
    }

    public static ConfigSyncPacket makePacket(){
        return new ConfigSyncPacket(grabHookEnabled, warhammerEnabled, cushionEnabled, teaEnabled,
                foodEnabled, confettiEnabled, enderFlaskEnabled, courtroomEnabled, carvedWoodEnabled,
                leatherStrawEnabled, bookcaseEnabled, coinsEnabled, scytheEnabled, spyglassEnabled,
                ropeEnabled, terracottaTileEnabled, omniChestEnabled
        );
    }

    public static boolean isGrabHookEnabled() {
        return grabHookEnabled;
    }

    public static boolean isWarhammerEnabled() {
        return warhammerEnabled;
    }

    public static boolean isCushionEnabled() {
        return cushionEnabled;
    }

    public static boolean isTeaEnabled() {
        return teaEnabled;
    }

    public static boolean isFoodEnabled() {
        return foodEnabled;
    }

    public static boolean isConfettiEnabled() {
        return confettiEnabled;
    }

    public static boolean isEnderFlaskEnabled() {
        return enderFlaskEnabled;
    }

    public static boolean isCourtroomEnabled() {
        return courtroomEnabled;
    }

    public static boolean isCarvedWoodEnabled() {
        return carvedWoodEnabled;
    }

    public static boolean isLeatherStrawEnabled() {
        return leatherStrawEnabled;
    }

    public static boolean isBookcaseEnabled() {
        return bookcaseEnabled;
    }

    public static boolean isCoinsEnabled() {
        return coinsEnabled;
    }

    public static boolean isScytheEnabled() {
        return scytheEnabled;
    }

    public static boolean isSpyglassEnabled() {
        return spyglassEnabled;
    }

    public static boolean isRopeEnabled() {
        return ropeEnabled;
    }

    public static boolean isTerracottaTileEnabled() {
        return terracottaTileEnabled;
    }

    public static boolean isOmniChestEnabled() {
        return omniChestEnabled;
    }
}
