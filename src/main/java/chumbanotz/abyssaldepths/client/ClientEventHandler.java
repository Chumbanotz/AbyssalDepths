package chumbanotz.abyssaldepths.client;

import chumbanotz.abyssaldepths.AbyssalDepths;
import chumbanotz.abyssaldepths.block.ADBlocks;
import chumbanotz.abyssaldepths.item.ADItems;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = AbyssalDepths.MOD_ID, value = Side.CLIENT)
public class ClientEventHandler {
	@SubscribeEvent
	public static void onRegisterModels(ModelRegistryEvent event) {
		registerItemModel(ADItems.GOGGLES);
		registerItemModel(ADItems.LONG_BILLFISH_SPIKE);
		registerItemModel(ADItems.SHORT_BILLFISH_SPIKE);
		registerItemModel(ADItems.SPIKE_SWORD);
		registerItemModel(ADItems.BUTTERFLYFISH);
		registerItemModel(ADItems.COOKED_BUTTERFLYFISH);
		registerItemModel(ADItems.MASKED_BUTTERFLYFISH);
		registerItemModel(ADItems.RACCOON_BUTTERFLYFISH);
		registerItemModel(ADItems.SPOTFIN_BUTTERFLYFISH);
		registerItemModel(ADItems.BANNERFISH);
		registerItemModel(ADItems.SEAWEED);
		ModelLoader.setCustomStateMapper(ADBlocks.SEAWEED, new StateMap.Builder().ignore(BlockLiquid.LEVEL).build());
	}

	private static void registerItemModel(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}