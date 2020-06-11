package chumbanotz.abyssaldepths.client;

import chumbanotz.abyssaldepths.client.model.ModelButterflyfish;
import chumbanotz.abyssaldepths.client.renderer.RenderBillfish;
import chumbanotz.abyssaldepths.client.renderer.RenderFish;
import chumbanotz.abyssaldepths.client.renderer.RenderNothing;
import chumbanotz.abyssaldepths.client.renderer.RenderSeaSerpent;
import chumbanotz.abyssaldepths.client.renderer.RenderSeahorse;
import chumbanotz.abyssaldepths.entity.BodyPart;
import chumbanotz.abyssaldepths.entity.SeaSerpent;
import chumbanotz.abyssaldepths.entity.Seahorse;
import chumbanotz.abyssaldepths.entity.billfish.Billfish;
import chumbanotz.abyssaldepths.entity.fish.Butterflyfish;
import chumbanotz.abyssaldepths.entity.fish.Fish;
import chumbanotz.abyssaldepths.util.IProxy;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy implements IProxy {
	@Override
	public void preInit() {
		RenderingRegistry.registerEntityRenderingHandler(SeaSerpent.class, RenderSeaSerpent::new);
		RenderingRegistry.registerEntityRenderingHandler(BodyPart.class, RenderNothing::new);
		RenderingRegistry.registerEntityRenderingHandler(Seahorse.class, RenderSeahorse::new);
		RenderingRegistry.registerEntityRenderingHandler(Fish.class, RenderFish::new);
		RenderingRegistry.registerEntityRenderingHandler(Seahorse.class, RenderSeahorse::new);
		RenderingRegistry.registerEntityRenderingHandler(Butterflyfish.class, manager -> new RenderFish(manager, new ModelButterflyfish(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(Billfish.class, RenderBillfish::new);
	}
}