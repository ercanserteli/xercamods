package xerca.xercamusic.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import net.minecraft.advancements.CriterionTrigger.Listener;

public class CustomTrigger implements CriterionTrigger<CustomTrigger.Instance>
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
    public void addPlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener)
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
    public void removePlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener)
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
    public void removePlayerListeners(PlayerAdvancements playerAdvancementsIn)
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
    public CustomTrigger.Instance createInstance(JsonObject json, DeserializationContext context)
    {
        return new CustomTrigger.Instance(getId());
    }

    /**
     * Trigger.
     *
     * @param parPlayer the player
     */
    public void trigger(ServerPlayer parPlayer)
    {
        CustomTrigger.Listeners xercamusictrigger$listeners = listeners.get(parPlayer.getAdvancements());

        if (xercamusictrigger$listeners != null)
        {
            xercamusictrigger$listeners.trigger(parPlayer);
        }
    }

    public static class Instance extends AbstractCriterionTriggerInstance
    {

        /**
         * Instantiates a new instance.
         */
        public Instance(ResourceLocation parRL)
        {
            super(parRL,  EntityPredicate.Composite.ANY);
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
        public void trigger(ServerPlayer player)
        {
            ArrayList<Listener<Instance>> list = null;

            for (Listener<Instance> listener : listeners)
            {
                if (listener.getTriggerInstance().test())
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
                    listener1.run(playerAdvancements);
                }
            }
        }
    }
}