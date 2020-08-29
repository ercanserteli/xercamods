package xerca.xercamod.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class CustomTrigger implements ICriterionTrigger<CustomTrigger.Instance>
{
    private final ResourceLocation resourceLocation;
    private final Map<PlayerAdvancements, CustomTrigger.Listeners> listeners = Maps.newHashMap();

    public CustomTrigger(String registryName)
    {
        super();
        this.resourceLocation = new ResourceLocation(registryName);
    }

    public CustomTrigger(ResourceLocation resourceLocation)
    {
        super();
        this.resourceLocation = resourceLocation;
    }

    @Override
    public ResourceLocation getId()
    {
        return resourceLocation;
    }

    @Override
    public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<CustomTrigger.Instance> listener)
    {
        CustomTrigger.Listeners myCustomTrigger$listeners = listeners.get(playerAdvancementsIn);

        if (myCustomTrigger$listeners == null)
        {
            myCustomTrigger$listeners = new CustomTrigger.Listeners(playerAdvancementsIn);
            listeners.put(playerAdvancementsIn, myCustomTrigger$listeners);
        }

        myCustomTrigger$listeners.add(listener);
    }

    @Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<CustomTrigger.Instance> listener)
    {
        CustomTrigger.Listeners xercamodtrigger$listeners = listeners.get(playerAdvancementsIn);

        if (xercamodtrigger$listeners != null)
        {
            xercamodtrigger$listeners.remove(listener);

            if (xercamodtrigger$listeners.isEmpty())
            {
                listeners.remove(playerAdvancementsIn);
            }
        }
    }

    @Override
    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn)
    {
        listeners.remove(playerAdvancementsIn);
    }

    @Override
    public Instance deserialize(JsonObject json, ConditionArrayParser conditions) {
        return new CustomTrigger.Instance(getId());
    }

    /**
     * Trigger.
     *
     * @param parPlayer the player
     */
    public void trigger(ServerPlayerEntity parPlayer)
    {
        CustomTrigger.Listeners xercamodtrigger$listeners = listeners.get(parPlayer.getAdvancements());

        if (xercamodtrigger$listeners != null)
        {
            xercamodtrigger$listeners.trigger(parPlayer);
        }
    }

    public static class Instance extends CriterionInstance
    {
        /**
         * Instantiates a new instance.
         */
        public Instance(ResourceLocation resourceLocation)
        {
            super(resourceLocation, EntityPredicate.AndPredicate.ANY_AND);
        }

        /**
         * Test.
         *
         * @return true, if successful
         */
        public boolean test()
        {
            return true;
        }
    }

    static class Listeners
    {
        private final PlayerAdvancements playerAdvancements;
        private final Set<ICriterionTrigger.Listener<CustomTrigger.Instance>> listeners = Sets.newHashSet();

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
        public void add(ICriterionTrigger.Listener<CustomTrigger.Instance> listener)
        {
            listeners.add(listener);
        }

        /**
         * Removes the listener.
         *
         * @param listener the listener
         */
        public void remove(ICriterionTrigger.Listener<CustomTrigger.Instance> listener)
        {
            listeners.remove(listener);
        }

        /**
         * Trigger.
         *
         * @param player the player
         */
        public void trigger(ServerPlayerEntity player)
        {
            ArrayList<ICriterionTrigger.Listener<CustomTrigger.Instance>> list = null;

            for (ICriterionTrigger.Listener<CustomTrigger.Instance> listener : listeners)
            {
                if (listener.getCriterionInstance().test())
                {
                    if (list == null)
                    {
                        list = Lists.newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null)
            {
                for (ICriterionTrigger.Listener<CustomTrigger.Instance> listener1 : list)
                {
                    listener1.grantCriterion(playerAdvancements);
                }
            }
        }
    }
}