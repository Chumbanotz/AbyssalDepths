package chumbanotz.abyssaldepths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chumbanotz.abyssaldepths.item.ADItems;
import chumbanotz.abyssaldepths.util.IProxy;
import chumbanotz.abyssaldepths.world.ADWorldGen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = AbyssalDepths.MOD_ID,
	name = "Abyssal Depths",
	version = "@VERSION@",
	acceptedMinecraftVersions = "[1.12.2]",
	dependencies = "required-after:minecraft;" + "required-after:forge@[14.23.5.2768,);")
public class AbyssalDepths {
	public static final String MOD_ID = "abyssaldepths";
	@SidedProxy(clientSide = "chumbanotz.abyssaldepths.client.ClientProxy", serverSide = "chumbanotz.abyssaldepths.ServerProxy")
	public static IProxy PROXY;
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(MOD_ID) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ADItems.SEAWEED);
		}

		@Override
		public void displayAllRelevantItems(NonNullList<ItemStack> items) {
			super.displayAllRelevantItems(items);
			for (EntityList.EntityEggInfo entitylist$entityegginfo : EntityList.ENTITY_EGGS.values()) {
				if (entitylist$entityegginfo.spawnedID.getNamespace().equals(MOD_ID)) {
					ItemStack itemstack = new ItemStack(Items.SPAWN_EGG, 1);
					ItemMonsterPlacer.applyEntityIdToItemStack(itemstack, entitylist$entityegginfo.spawnedID);
					items.add(itemstack);
				}
			}
		}
	};

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		PROXY.preInit();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		for (Item butterflyfish : new Item[] {ADItems.BUTTERFLYFISH, ADItems.MASKED_BUTTERFLYFISH, ADItems.RACCOON_BUTTERFLYFISH, ADItems.SPOTFIN_BUTTERFLYFISH}) {
			GameRegistry.addSmelting(butterflyfish, new ItemStack(ADItems.COOKED_BUTTERFLYFISH), 0.35F);
		}

		if (ADConfig.GENERATION.generateRocks || ADConfig.GENERATION.generateSeaweed) {
			for (int i = 0; i < 3; i++) {
				GameRegistry.registerWorldGenerator(new ADWorldGen(i), i);
			}
		}
	}

	public static ResourceLocation prefix(String name) {
		return new ResourceLocation(MOD_ID, name);
	}

	public static ResourceLocation getEntityTexture(String name) {
		return prefix("textures/entity/" + name + ".png");
	}
}