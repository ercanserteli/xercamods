package xerca.xercamod.common;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SeedLootModifier extends LootModifier {
    private final Item itemSeed;
    private final boolean isFood;
    public static final Supplier<Codec<SeedLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).and(
            inst.group(
                    ForgeRegistries.ITEMS.getCodec().fieldOf("seedItem").forGetter(m -> m.itemSeed),
                    Codec.BOOL.fieldOf("isFood").forGetter(m -> m.isFood)
            )).apply(inst, SeedLootModifier::new)
    ));

    public static Codec<SeedLootModifier> makeCodec() {
        return RecordCodecBuilder.create(inst -> codecStart(inst).and(
                inst.group(
                        ForgeRegistries.ITEMS.getCodec().fieldOf("seedItem").forGetter(m -> m.itemSeed),
                        Codec.BOOL.fieldOf("isFood").forGetter(m -> m.isFood)
                )).apply(inst, SeedLootModifier::new));
    }

    protected SeedLootModifier(LootItemCondition[] conditionsIn, Item itemSeed, boolean isFood) {
        super(conditionsIn);
        this.itemSeed = itemSeed;
        this.isFood = isFood;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if((isFood && !Config.isFoodEnabled()) || (!isFood && !Config.isTeaEnabled())){
            return generatedLoot;
        }
        generatedLoot.add(new ItemStack(itemSeed));
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
