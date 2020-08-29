package xerca.xercamusic.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
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
    private final ResourceLocation RL;
    private final Map<PlayerAdvancements, Listeners> listeners = Maps.newHashMap();

    CustomTrigger(String registryName)
    {
        super();
        RL = new ResourceLocation(registryName);
    }

    public CustomTrigger(ResourceLocation parRL)
    {
        super();
        RL = parRL;
    }

    @Override
    public ResourceLocation getId()
    {
        return RL;
    }

    @Override
    public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener)
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
    public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener)
    {
        CustomTrigger.Listeners xercamusictrigger$listeners = listeners.get(playerAdvancementsIn);

        if (xercamusictrigger$listeners != null)
        {
            xercamusictrigger$listeners.remove(listener);

            if (xercamusictrigger$listeners.isEmpty())
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

    /**
     * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
     *
     * @param json the json
     * @param context the context
     * @return the tame bird trigger. instance
     */
    @Override
    public CustomTrigger.Instance deserialize(JsonObject json, ConditionArrayParser context)
    {
        return new CustomTrigger.Instance(getId());
    }

    /**
     * Trigger.
     *
     * @param parPlayer the player
     */
    public void trigger(ServerPlayerEntity parPlayer)
    {
        CustomTrigger.Listeners xercamusictrigger$listeners = listeners.get(parPlayer.getAdvancements());

        if (xercamusictrigger$listeners != null)
        {
            xercamusictrigger$listeners.trigger(parPlayer);
        }
    }

    public static class Instance extends CriterionInstance
    {

        /**
         * Instantiates a new instance.
         */
        public Instance(ResourceLocation parRL)
        {
            super(parRL,  EntityPredicate.AndPredicate.ANY_AND);
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
        private final Set<Listener<Instance>> listeners = Sets.newHashSet();

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
        public void add(Listener<Instance> listener)
        {
            listeners.add(listener);
        }

        /**
         * Removes the listener.
         *
         * @param listener the listener
         */
        public void remove(Listener<Instance> listener)
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
            ArrayList<Listener<Instance>> list = null;

            for (Listener<Instance> listener : listeners)
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
                for (Listener<Instance> listener1 : list)
                {
                    listener1.grantCriterion(playerAdvancements);
                }
            }
        }
    }
}