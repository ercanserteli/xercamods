package xerca.xercamod.common;

import java.nio.file.Path;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber
public class Config {
    public static final String CATEGORY_GENERAL = "general";

    private static final ForgeConfigSpec.Builder COMMON_BUILD = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;


    public static ForgeConfigSpec.BooleanValue GRAB_HOOK_ENABLE;
    public static ForgeConfigSpec.BooleanValue WARHAMMER_ENABLE;
    public static ForgeConfigSpec.BooleanValue CUSHION_ENABLE;
    public static ForgeConfigSpec.BooleanValue TEA_ENABLE;
    public static ForgeConfigSpec.BooleanValue FOOD_ENABLE;
    public static ForgeConfigSpec.BooleanValue CONFETTI_ENABLE;
    public static ForgeConfigSpec.BooleanValue ENDER_FLASK_ENABLE;
    public static ForgeConfigSpec.BooleanValue COURTROOM_ENABLE;
    public static ForgeConfigSpec.BooleanValue CARVED_WOOD_ENABLE;
    public static ForgeConfigSpec.BooleanValue LEATHER_STRAW_ENABLE;
    public static ForgeConfigSpec.BooleanValue BOOKCASE_ENABLE;
    public static ForgeConfigSpec.BooleanValue COINS_ENABLE;


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
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        System.out.println("Config load event");
        System.out.println(COMMON_CONFIG.getValues());
    }

    @SubscribeEvent
    public static void onReload(final ModConfig.ConfigReloading configEvent) {
        System.out.println("Config load event");
        System.out.println(COMMON_CONFIG.getValues());
    }

}
