package xerca.xercamod.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class CustomTrigger implements CriterionTrigger<CustomTrigger.Instance>
{
    private final ResourceLocation resourceLocation;
    private final Map<PlayerAdvancements, CustomTrigger.Listeners> listeners = Maps.newHashMap();

    public CustomTrigger(String registryName)
    {
        super();
        this.resourceLocation = new ResourceLocation(registryName);
    }

    @Override
    public @NotNull ResourceLocation getId()
    {
        return resourceLocation;
    }

    @Override
    public void addPlayerListener(@NotNull PlayerAdvancements playerAdvancementsIn, CriterionTrigger.@NotNull Listener<CustomTrigger.Instance> listener)
    {
        CustomTrigger.Listeners myCustomTrigger$listeners = listeners.get(playerAdvancementsIn);

        if (myCustomTrigger$listeners == null) {
            myCustomTrigger$listeners = new CustomTrigger.Listeners(playerAdvancementsIn);
            listeners.put(playerAdvancementsIn, myCustomTrigger$listeners);
        }

        myCustomTrigger$listeners.add(listener);
    }

    @Override
    public void removePlayerListener(@NotNull PlayerAdvancements playerAdvancementsIn, CriterionTrigger.@NotNull Listener<CustomTrigger.Instance> listener)
    {
        CustomTrigger.Listeners xercamodtrigger$listeners = listeners.get(playerAdvancementsIn);

        if (xercamodtrigger$listeners != null) {
            xercamodtrigger$listeners.remove(listener);
            if (xercamodtrigger$listeners.isEmpty()) {
                listeners.remove(playerAdvancementsIn);
            }
        }
    }

    @Override
    public void removePlayerListeners(@NotNull PlayerAdvancements playerAdvancementsIn)
    {
        listeners.remove(playerAdvancementsIn);
    }

    @Override
    public @NotNull Instance createInstance(@NotNull JsonObject json, @NotNull DeserializationContext conditions) {
        return new CustomTrigger.Instance(getId());
    }

    /**
     * Trigger.
     *
     * @param parPlayer the player
     */
    public void trigger(ServerPlayer parPlayer)
    {
        CustomTrigger.Listeners xercamodtrigger$listeners = listeners.get(parPlayer.getAdvancements());

        if (xercamodtrigger$listeners != null) {
            xercamodtrigger$listeners.trigger(parPlayer);
        }
    }

    public static class Instance extends AbstractCriterionTriggerInstance
    {
        /**
         * Instantiates a new instance.
         */
        public Instance(ResourceLocation resourceLocation)
        {
            super(resourceLocation, EntityPredicate.Composite.ANY);
        }
    }

    static class Listeners
    {
        private final PlayerAdvancements playerAdvancements;
        private final Set<CriterionTrigger.Listener<CustomTrigger.Instance>> listeners = Sets.newHashSet();

        /**
         * Instantiates a new listeners.
         *
         * @param playerAdvancementsIn the player advancements in
         */
        public Listeners(PlayerAdvancements playerAdvancementsIn)
        {
            playerAdvancements = playerAdvancementsIn;
        }

        /**
         * Checks if is empty.
         *
         * @return true, if is empty
         */
        public boolean isEmpty()
        {
            return listeners.isEmpty();
        }

        /**
         * Adds the listener.
         *
         * @param listener the listener
         */
        public void add(CriterionTrigger.Listener<CustomTrigger.Instance> listener)
        {
            listeners.add(listener);
        }

        /**
         * Removes the listener.
         *
         * @param listener the listener
         */
        public void remove(CriterionTrigger.Listener<CustomTrigger.Instance> listener)
        {
            listeners.remove(listener);
        }

        /**
         * Trigger.
         *
         * @param ignoredPlayer the player
         */
        public void trigger(ServerPlayer ignoredPlayer)
        {
            ArrayList<CriterionTrigger.Listener<CustomTrigger.Instance>> list = null;

            for (CriterionTrigger.Listener<CustomTrigger.Instance> listener : listeners) {
                if (list == null) {
                    list = Lists.newArrayList();
                }

                list.add(listener);
            }

            if (list != null) {
                for (CriterionTrigger.Listener<CustomTrigger.Instance> listener1 : list) {
                    listener1.run(playerAdvancements);
                }
            }
        }
    }
}