package xerca.xercamusic.client;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import xerca.xercamusic.common.Proxy;
import xerca.xercamusic.common.entity.Entities;

public class ClientProxy implements Proxy {
	@Override
	public void preInit() {
    	RenderingRegistry.registerEntityRenderingHandler(Entities.MUSIC_SPIRIT, new RenderNothingFactory());
	}
}
