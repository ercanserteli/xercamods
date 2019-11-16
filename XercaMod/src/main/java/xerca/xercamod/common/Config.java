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

    private static final ForgeConfigSpec.Builder SERVER_BUILD = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder COMMON_BUILD = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec SERVER_CONFIG;
    public static ForgeConfigSpec COMMON_CONFIG;


    public static ForgeConfigSpec.BooleanValue GRAB_HOOK_ENABLE;
    public static ForgeConfigSpec.BooleanValue WARHAMMER_ENABLE;


    static {
        SERVER_BUILD.comment("General settings").push(CATEGORY_GENERAL);

        GRAB_HOOK_ENABLE = SERVER_BUILD.comment("Enable Grab Hook").define("grab_hook", true);
        WARHAMMER_ENABLE = SERVER_BUILD.comment("Enable Warhammer").define("warhammer", true);

        SERVER_BUILD.pop();

        SERVER_CONFIG = SERVER_BUILD.build();
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

    }

    @SubscribeEvent
    public static void onReload(final ModConfig.ConfigReloading configEvent) {
    }

}
