package chumbanotz.abyssaldepths;

import chumbanotz.abyssaldepths.block.ADBlocks;
import chumbanotz.abyssaldepths.block.Seaweed;
import chumbanotz.abyssaldepths.entity.BodyPart;
import chumbanotz.abyssaldepths.entity.SeaSerpent;
import chumbanotz.abyssaldepths.entity.Seahorse;
import chumbanotz.abyssaldepths.entity.billfish.Sailfish;
import chumbanotz.abyssaldepths.entity.billfish.Swordfish;
import chumbanotz.abyssaldepths.entity.fish.Basslet;
import chumbanotz.abyssaldepths.entity.fish.Butterflyfish;
import chumbanotz.abyssaldepths.entity.fish.Clownfish;
import chumbanotz.abyssaldepths.entity.fish.CommonFish;
import chumbanotz.abyssaldepths.item.Goggles;
import chumbanotz.abyssaldepths.item.SpikeSword;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod.EventBusSubscriber(modid = AbyssalDepths.MOD_ID)
public class RegistryHandler {
	private static int entityId;

	@SubscribeEvent
	public static void onBlockRegistry(RegistryEvent.Register<Block> event) {
		event.getRegistry().register(setRegistryName("seaweed", new Seaweed()));
	}

	@SubscribeEvent
	public static void onItemRegistry(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(
				setRegistryName("seaweed", new ItemBlock(ADBlocks.SEAWEED)),
				setRegistryName("goggles", new Goggles()),
				setRegistryName("short_billfish_spike", new Item()),
				setRegistryName("long_billfish_spike", new Item()),
				setRegistryName("spike_sword", new SpikeSword()),
				setRegistryName("bannerfish", new ItemFood(3, 0.4F, false)),
				setRegistryName("butterflyfish", new ItemFood(2, 0.1F, false)),
				setRegistryName("cooked_butterflyfish", new ItemFood(6, 0.6F, false)),
				setRegistryName("masked_butterflyfish", new ItemFood(2, 0.1F, false)),
				setRegistryName("raccoon_butterflyfish", new ItemFood(2, 0.1F, false)),
				setRegistryName("spotfin_butterflyfish", new ItemFood(2, 0.1F, false))
				);
	}

	@SubscribeEvent
	public static void onEntityEntryRegistry(RegistryEvent.Register<EntityEntry> event) {
		event.getRegistry().registerAll(
				createEntityEntry("body_part", BodyPart.class).tracker(80, 1, true).build(),
				createEntityEntry("fish", CommonFish.class, 8762546, 12307920, ADConfig.SPAWNS.fishWeight, 3, 5).build(),
				createEntityEntry("clownfish", Clownfish.class, 13785366, 16777215, ADConfig.SPAWNS.clownfishWeight, 2, 3).build(),
				createEntityEntry("seahorse", Seahorse.class, 13818758, 7833651, ADConfig.SPAWNS.seahorseWeight, 1, 3).build(),
				createEntityEntry("butterflyfish", Butterflyfish.class, 8762546, 12307920, ADConfig.SPAWNS.butterflyfishWeight, 2, 5).build(),
				createEntityEntry("masked_butterflyfish", Butterflyfish.Masked.class, 16438016, 3817060, ADConfig.SPAWNS.maskedButterflyfishWeight, 2, 5).build(),
				createEntityEntry("raccoon_butterflyfish", Butterflyfish.Raccoon.class, 16297728, 1513499, ADConfig.SPAWNS.raccoonButterflyfishWeight, 3, 5).build(),
				createEntityEntry("spotfin_butterflyfish", Butterflyfish.Spotfin.class, 16311296, 16121335, ADConfig.SPAWNS.spotfinButterflyfishWeight, 3, 5).build(),
				createEntityEntry("bannerfish", Butterflyfish.Banner.class, 16183797, 1184274, ADConfig.SPAWNS.bannerfishWeight, 2, 5).build(),
				createEntityEntry("fairy_basslet", Basslet.Fairy.class, 12599507, 16693761, ADConfig.SPAWNS.fairyBassletWeight, 1, 4).build(),
				createEntityEntry("blackcap_basslet", Basslet.BlackCap.class, 12599507, 1184274, ADConfig.SPAWNS.blackcapBassletWeight, 1, 4).build(),
				createEntityEntry("sailfish", Sailfish.class, 1060456, 14737632, ADConfig.SPAWNS.sailfishWeight, 1, 5).build(),
				createEntityEntry("swordfish", Swordfish.class, 1060456, 14737632, ADConfig.SPAWNS.swordfishWeight, 1, 4).build(),
				createEntityEntry("sea_serpent", SeaSerpent.class, 5938242, 9398119, ADConfig.SPAWNS.seaSerpentWeight, 1, 2).build()
				);
	}

	private static <T extends EntityLiving> EntityEntryBuilder<?> createEntityEntry(String name, Class<T> entityClass, int eggPrimary, int eggSecondary, int weight, int min, int max) {
		addSpawn(entityClass, weight, min, max);
		return createEntityEntry(name, entityClass).egg(eggPrimary, eggSecondary).tracker(80, 3, true);
	}

	private static <T extends Entity> EntityEntryBuilder<T> createEntityEntry(String name, Class<T> entityClass) {
		if (EntityLiving.class.isAssignableFrom(entityClass)) {
			LootTableList.register(AbyssalDepths.prefix("entities/" + name));
			EntitySpawnPlacementRegistry.setPlacementType(entityClass, EntityLiving.SpawnPlacementType.IN_WATER);
		}

		return EntityEntryBuilder.<T>create().entity(entityClass).id(AbyssalDepths.prefix(name), entityId++).name(AbyssalDepths.MOD_ID + '.' + name);
	}

	private static void addSpawn(Class<? extends EntityLiving> entityClass, int weight, int min, int max) {
		if (weight <= 0) {
			return;
		}

		for (Biome biome : ForgeRegistries.BIOMES) {
			boolean add = false;
			if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER) || BiomeDictionary.hasType(biome, BiomeDictionary.Type.END) || BiomeDictionary.hasType(biome, BiomeDictionary.Type.MUSHROOM) || BiomeDictionary.hasType(biome, BiomeDictionary.Type.VOID)) {
				continue;
			}

			if (entityClass == CommonFish.class) {
				add = BiomeDictionary.hasType(biome, BiomeDictionary.Type.WATER);
			} else {
				add = BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN);
			}

			if (add) {
				biome.getSpawnableList(EnumCreatureType.WATER_CREATURE).add(new Biome.SpawnListEntry(entityClass, MathHelper.clamp(weight, 1, 100), min, max));
			}
		}
	}

	public static <T extends IForgeRegistryEntry<T>> T setRegistryName(String name, T entry) {
		ResourceLocation registryName = AbyssalDepths.prefix(name);
		if (entry instanceof Block) {
			((Block)entry).setTranslationKey(AbyssalDepths.MOD_ID + '.' + registryName.getPath());
			((Block)entry).setCreativeTab(AbyssalDepths.CREATIVE_TAB);
		}

		if (entry instanceof Item) {
			((Item)entry).setTranslationKey(AbyssalDepths.MOD_ID + '.' + registryName.getPath());
			((Item)entry).setCreativeTab(AbyssalDepths.CREATIVE_TAB);
		}

		return entry.setRegistryName(registryName);
	}
}