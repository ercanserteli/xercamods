package xerca.xercamod.common;

import net.minecraftforge.event.entity.EntityEvent;
import xerca.xercamod.common.entity.EntityHook;

public class HookReturningEvent extends EntityEvent {

    public HookReturningEvent(EntityHook entity) {
        super(entity);
    }

}
